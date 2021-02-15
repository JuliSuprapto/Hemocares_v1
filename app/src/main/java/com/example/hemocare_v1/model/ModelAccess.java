package com.example.hemocare_v1.model;

public class ModelAccess {

    String _id, nik, fullname, username, password, bloodtype, phone, address, birthdate, profilephoto, email, role;

    public ModelAccess(String _id, String nik, String fullname, String username, String password, String bloodtype, String phone, String address, String birthdate, String profilephoto, String email, String role) {
        this._id = _id;
        this.nik = nik;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.bloodtype = bloodtype;
        this.phone = phone;
        this.address = address;
        this.birthdate = birthdate;
        this.profilephoto = profilephoto;
        this.email = email;
        this.role = role;
    }

    public ModelAccess() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
