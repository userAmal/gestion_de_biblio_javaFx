package Models;

public class Admin {
int id;
String UserName;
String pasPassword;

public Admin(int id, String userName, String pasPassword) {
   this.id = id;
   this.UserName = userName;
   this.pasPassword = pasPassword;
}

public int getId() {
   return this.id;
}

public void setId(int id) {
   this.id = id;
}

public String getUserName() {
   return this.UserName;
}

public void setUserName(String userName) {
   this.UserName = userName;
}

public String getPasPassword() {
   return this.pasPassword;
}

public void setPasPassword(String pasPassword) {
   this.pasPassword = pasPassword;
}
}

