## 🔐 Password Security Vault

A secure password manager built in **Java**, featuring:

- **PBKDF2 (SHA-256 + Salt)** for master password hashing
- **AES-256 encryption** for protecting stored account passwords
- **Export/Import with passphrase encryption**
- **Built-in strong password generator**

This project demonstrates **Java security, encryption, and file handling** in a simple console-based application.

---

## ✨ Features

✅ Master Password Protection (PBKDF2WithHmacSHA256 + Salt)  
✅ AES-256 Encryption for stored account passwords  
✅ Add, View, Search, and Delete passwords  
✅ Change master password securely  
✅ Export/Import vault with passphrase encryption  
✅ Built-in strong password generator  
✅ Input validation & meaningful error messages  
✅ Clean structure (`Main.java`, `Auth.java`, `Vault.java`)

---

## 📂 Project Structure

    Password-Security-Vault/
    │── Main.java      # Entry point, handles user interface
    │── Auth.java      # Manages master password creation, verification & hashing
    │── Vault.java     # Encrypts, decrypts & manages account passwords
    │── vault.txt      # Encrypted password storage (auto-created, ignored in Git)
    │── vault.key      # AES encryption key (auto-generated, ignored in Git)
    │── master.key     # Stores master password hash + salt (ignored in Git)

---

## 🚀 How to Run

1. Clone the repository
    ```
    git clone https://github.com/tushar05g/Password-Security-Vault.git
    cd Password-Security-Vault
    ```

2. Compile the code
    ```
    javac Main.java Auth.java Vault.java
    ```

3. Run the project
    ```
    java Main
    ```

---

## 🛠 Menu Options

**== Password Vault ==**
1. Add Password
2. View Passwords
3. Delete Password
4. Search Password
5. Change Master Password
6. Export Vault
7. Import Vault
8. Generate Strong Password
9. Exit

---

## 🔒 Security Notes

- Master password is hashed with **PBKDF2WithHmacSHA256 + Salt** before storage.
- Account passwords are encrypted with **AES-256 (CBC + IV)** before saving to `vault.txt`.
- AES key is generated once and stored securely in `vault.key`.
- Exported vaults are encrypted with a **user-provided passphrase**.
- Sensitive files (`vault.txt`, `vault.key`, `master.key`) are **ignored in Git** using `.gitignore`.
- Even if `vault.txt` is opened, it only contains **Base64 ciphertext**.

---

## 📌 Roadmap / Future Enhancements

🔹 GUI-based version (JavaFX or Swing)  
🔹 Cloud sync with client-side encryption  
🔹 Password strength meter & breach check  
🔹 Cross-platform release (packaged JAR)  
🔹 Two-factor authentication (2FA) for master login


