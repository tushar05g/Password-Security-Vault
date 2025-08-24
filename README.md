## 🔐 Password Security Vault

A secure password manager built in Java, featuring:

- PBKDF2 (SHA-256) for safe master password hashing

- AES encryption for protecting stored account passwords

This project demonstrates Java security, encryption, and file handling in a simple console-based application.

---
## ✨ Features

✅ Master Password Protection (PBKDF2WithHmacSHA256 + Salt)

✅ AES Encryption for stored account passwords

✅ Add, View, Search, and Delete passwords

✅ Change master password securely

✅ Input validation & meaningful error messages

✅ Clean structure (Main.java, Auth.java, Vault.java)

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

2. Compile the code
    ```
    javac Main.java Auth.java Vault.java

3. Run the project
    ```
    java Main

---
## 🛠 Menu Options
**== Password Vault ==**
1. Add Password
2. View Passwords
3. Delete Password
4. Search Password
5. Change Master Password
6. Exit

---
## 🔒 Security Notes

- Master password is hashed with PBKDF2 + Salt before storage.
- Account passwords are AES-encrypted before saving to vault.txt.
- AES key is auto-generated and stored in vault.key.
- Sensitive files (vault.txt, vault.key, master.key) are ignored in Git using .gitignore.
- Even if vault.txt is opened, it only contains Base64-encoded ciphertext.

---
## 📌 Roadmap / Future Enhancements

🔹 GUI-based version (JavaFX or Swing)

🔹 Stronger AES key size (256-bit)

🔹 Export/Import vault with encryption

🔹 Built-in password generator

🔹 Cross-platform release (packaged JAR)

---
### 👨‍💻 Author

    Tushar Goyal
    📧 tushargoyal253@gmail.com