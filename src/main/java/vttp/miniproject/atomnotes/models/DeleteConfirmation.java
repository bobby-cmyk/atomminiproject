package vttp.miniproject.atomnotes.models;

import jakarta.validation.constraints.Pattern;

public class DeleteConfirmation {
    
    @Pattern(message="Does not match confirmation code above", regexp="delete-all-completed-tasks")
    private String confirmationMessage;

    public String getConfirmationMessage() {return confirmationMessage;}
    public void setConfirmationMessage(String confirmationMessage) {this.confirmationMessage = confirmationMessage;}
}
