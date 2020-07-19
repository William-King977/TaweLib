import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the Borrowed Items page.
 * The user can view their current loans.
 * @author William King
 */
public class BorrowedItemsController {
	
	/** Holds all the loans from the loans file for local storage. */
    private ArrayList<Loan> loanList;
    
    /** Holds all the user's current loans. */
    private ArrayList<Loan> userLoans = new ArrayList<>();
    
    /** Holds the current user's username for local storage. */
    private String username;
    
	/** A list view used to display all of the loans. */
	@FXML private ListView<String> lstShowLoans;
	
	/** The back button for the page. */
    @FXML private Button btnBack;
    
    /** A text field used to display the selected loan's ID. */
    @FXML private TextField txtLoanID;
    
    /** A text field used to display the selected loan's resource ID. */
    @FXML private TextField txtResourceID;
    
    /** A text field used to display the loan's due date. */
    @FXML private TextField txtDueDate;
    
    /** A text field used to display the loan's checkout date. */
    @FXML private TextField txtCheckoutDate;
    
    /** A text field used to display the ID of the librarian who 
     * authorised the loan. */
    @FXML private TextField txtStaffID;
    
    /** A text field used to display the ID of the borrowed copy. */
    @FXML private TextField txtCopyID;
    
    /** A text field used to display the borrowed copy's resource type. */
    @FXML private TextField txtResourceType;
    
    /**
	 * Sets up the array lists for the Loans and display 
	 * the user's current loans.
	 * This method will run automatically.
	 */
	public void initialize() { 
		loanList = FileHandling.getLoans();
		username = FileHandling.getCurrentUser();
		
		for (Loan loan : loanList) {
			if (username.equals(loan.getUsername()) && !loan.isReturned()) {
				userLoans.add(loan);
				String strLoan = "Loan ID: " + loan.getLoanID() + " | "
						+ "Checkout Date: " + loan.getCheckoutDate();
				lstShowLoans.getItems().add(strLoan);
			}
		}	
	}	
	
	/**
	 * Displays all the details of a selected loan.
	 */
	public void displayLoanDetails() {
		int selectedIndex = lstShowLoans.getSelectionModel()
				.getSelectedIndex();
    	// If nothing was selected i.e. clicking the list view.
		if (selectedIndex < 0) {
			return;
		} 
		
		// Get selected loan and show its details.
		Loan selectedLoan = userLoans.get(selectedIndex);
		
		txtLoanID.setText(selectedLoan.getLoanID() + "");
		txtCopyID.setText(selectedLoan.getCopyID() + "");
		txtResourceID.setText(selectedLoan.getResourceID() + "");
		txtResourceType.setText(selectedLoan.getType() + "");
		
		txtCheckoutDate.setText(selectedLoan.getCheckoutDate());
		
		if (selectedLoan.getDueDate().isEmpty()) {
			txtDueDate.setText("N/A");
		} else {
			txtDueDate.setText(selectedLoan.getDueDate());
		}
		
		txtStaffID.setText(selectedLoan.getStaffID() + "");
	}
	
	/**
	 * Goes back to the previous page when the button is clicked.
	 * @throws IOException Throws an exception to be caught when the 
	 *         FXML file isn't available.
	 */
	public void handleBackButtonAction() throws IOException {
		// Closes the window.
		Stage stage = (Stage) btnBack.getScene().getWindow();
		stage.close();
		Stage primaryStage = new Stage();
		Parent root = FXMLLoader.load(getClass()
				.getResource("FXMLFiles/UserDashboard.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show(); // Displays the new stage.
	}
}
