package controllers;

import java.time.LocalDate;
import java.util.LinkedHashMap;

import data.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the New User page. 
 * Allows the librarian to create new users for the system.
 * @author William King
 */
public class NewUserController {
	/** Holds the file name of the default profile picture. */
	private final String DEFAULT_PROFILE_PICTURE = "Default1.png";
	/** Holds the type of user being created. */
	private String newUserType = "null";
	/** Holds the new user's username. */
	private String newUsername;
	
	/** A list of all the librarians. */
	private LinkedHashMap<String, Librarian> librarianList;
	/** A list of all the members. */
	private LinkedHashMap<String, User> userList;
	
	/** A text field to hold the user's username. */
	@FXML private TextField txtUsername;
	/** A text field to hold the user's first name. */
	@FXML private TextField txtFirstName;
	/** A text field to hold the user's surname. */
	@FXML private TextField txtSurname;
	/** A text field to hold the user's address line 1. */
	@FXML private TextField txtAddressLine1;
	/** A text field to hold the user's address line 2. */
	@FXML private TextField txtAddressLine2;
	/** A text field to hold the residing city of the user. */
	@FXML private TextField txtCity;
	/** A text field to hold the user's postcode. */
	@FXML private TextField txtPostcode;
	/** A text field to hold the user's UK mobile number. */
	@FXML private TextField txtMobileNumber;
	
	/** A check box to see if the newly created user is a librarian or a 
	 * member. */
	@FXML private CheckBox cbStaff;
	/** The back button for the page. */
	@FXML private Button btnBack;
	
	/**
	 * Creates a new user based on the information typed in 
	 * and saves the user into the appropriate file system.
	 */
	public void handleSaveButtonAction() {
		//Fetches all the entered information from the text fields.
		String username = txtUsername.getText().trim();
		String firstName = txtFirstName.getText().trim();
		String surname = txtSurname.getText().trim();
		String mobileNumber = txtMobileNumber.getText().trim();
		String city = txtCity.getText().trim();
		String postcode = txtPostcode.getText().trim();
		String address1 = txtAddressLine1.getText().trim();
		String address2 = txtAddressLine2.getText().trim();
		
		// Set address2 to 'N/A' if it's empty.
		if (address2.isEmpty()) {
			address2 = "N/A";
		}
		
		String profilePicture = DEFAULT_PROFILE_PICTURE;
		double fine = 0.00;
		
		// Validation rules applied to certain fields.
		boolean requiredFilled = Utility.isFieldFilledUser(firstName, surname, 
				mobileNumber, address1, city, postcode);
		boolean hasLetter = Utility.isAlphaUser(firstName, surname, 
				mobileNumber, address1, address2, city); 
		boolean validPostcode = Utility.isPostcodeValid(postcode);
		boolean usernameExist = Utility.isUsernameExist(userList, 
				librarianList, username);
		
		// Shows appropriate alerts if validation has not been met.
		if (!requiredFilled) { 
			Alerts.missingFields();
			return;
		} else if (!hasLetter) {
			Alerts.nonAlphaError();
			return;
		} else if (!validPostcode) {
			Alerts.invalidPostcode();
			return;
		} else if (usernameExist) {
			Alerts.usernameExists();
			return;
		}
		
		saveUser(username, firstName, surname, mobileNumber, address1, 
				address2, city, postcode, profilePicture, fine);
	}
	
	/**
	 * Registers a new user and saves them to the system.
	 * @param username The entered username.
	 * @param firstName The entered first name.
	 * @param surname The entered surname.
	 * @param mobileNumber The entered mobile number.
	 * @param address1 The entered address line 1.
	 * @param address2 The entered address line 2.
	 * @param city The entered city.
	 * @param postcode The entered post code.
	 * @param profilePicture The default profile picture for the user.
	 * @param fine The default fine set for the user.
	 */
	private void saveUser(String username, String firstName, String surname, 
			String mobileNumber, String address1, String address2, String city, 
			String postcode, String profilePicture, double fine) {
		newUsername = username;
		String newUser = "";
		int userType;
		if (cbStaff.isSelected()) {
			// Gets todays date in format of YYYY-MM-DD.
			String employmentDate = LocalDate.now().toString(); 
			int staffID;
			int previousStaffID;
			// Gets the staff ID of the latest registered librarian.
			if (librarianList.size() == 0) { // staffID will start at 1.
				staffID = 1;
			} else {
				previousStaffID = getLatestStaffID();
				staffID = previousStaffID + 1; 
			}
			
			userType = 1;
			newUserType = "Librarian";
			Librarian newLibrarian = new Librarian(username, firstName, surname,
					mobileNumber, address1, address2, city, postcode, 
					profilePicture, fine, staffID, employmentDate);
			newUser = newLibrarian.toStringDetail();
			librarianList.put(username, newLibrarian);
		// If it's a regular user.
		} else {
			userType = 2;
			newUserType = "User";
			User newMember = new User(username, firstName, surname, mobileNumber,
					address1, address2, city, postcode, profilePicture, fine);
			newUser = newMember.toStringDetail();
			userList.put(username, newMember);
		}
		FileHandling.createUser(newUser, userType);
		Alerts.userCreated();
		handleBackButtonAction();
	}
	
	/**
	 * Gets the staff ID of the latest librarian.
	 * @return StaffID of the latest librarian.
	 */
	public int getLatestStaffID() {
		int staffID = -1;
		Librarian latestStaff = null;
		
		// Loop through each key (basically till the last one).
		for (String key : librarianList.keySet()) {
			latestStaff = librarianList.get(key);
		}
		staffID = latestStaff.getStaffID();
		return staffID;
	}
	
	/**
	 * Sets the Linked Hashmaps for the users so that the new user can be added locally.
	 * @param userList The Linked Hashmap of all current members.
	 * @param librarianList The Linked Hashmap of all current librarians.
	 */
	public void setUserLists(LinkedHashMap<String, User> userList, 
			LinkedHashMap<String, Librarian> librarianList) {
		// Lists are passed in as the page is accessed.
		this.userList = userList;
		this.librarianList = librarianList;
	}
	
	/**
	 * Gets the type of user being created.
	 * @return The type of user being created.
	 */
	public String getNewUserType() {
		return newUserType;
	}
	
	/**
	 * Gets the new user's username.
	 * @return The username of the new user.
	 */
	public String getNewUsername() {
		return newUsername;
	}
	
	/**
	 * Closes the current page.
	 */
	public void handleBackButtonAction() {
		Stage curStage = (Stage) btnBack.getScene().getWindow(); 
		curStage.close(); 
	}
}