package vttp.miniproject.atomnotes.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignUpForm {

    @NotBlank(message="Username cannot be empty")
    @Size(message="Minimum 5 and maximum 15 characters", 
        min=5, max=15)
    private String username;

    @NotBlank(message="Password cannot be empty")
    @Pattern(message="Minimum 8 characters, at least 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character", 
        regexp="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    private String password;

    @NotNull(message="Passwords do not match")
    private String confirmPassword;

    @Email(message = "Email is not valid", 
        regexp="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String email;

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password; checkPassword();}

    public String getConfirmPassword() {return confirmPassword;}
    public void setConfirmPassword(String confirmPassword) {this.confirmPassword = confirmPassword; checkPassword();}
    
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    private void checkPassword() {
        if(this.password == null || this.confirmPassword == null){
            return;
        }
        else if(!this.password.equals(confirmPassword)){
            this.confirmPassword = null;
        }
    }
}
