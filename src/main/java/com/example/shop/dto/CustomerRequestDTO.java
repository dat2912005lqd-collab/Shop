package com.example.shop.dto;
import jakarta.validation.constraints.*;
public class CustomerRequestDTO {

    @NotBlank(message = "Name is required")
    private String fullName;
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone number should be 10 digits"
    )
    private String phone;
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Address is required")
    private String address;
public CustomerRequestDTO() {}
public String getFullName() {
    return fullName;
}
public String getPhone() {
    return phone;
}
public String getEmail() {
    return email;
}
public String getAddress() {
    return address;
}
public void setFullName(String fullName) {
    this.fullName = fullName;
}
public void setPhone(String phone) {
    this.phone = phone;
}
public void setEmail(String email) {
    this.email = email;
}
public void setAddress(String address) {
    this.address = address;
}
}
