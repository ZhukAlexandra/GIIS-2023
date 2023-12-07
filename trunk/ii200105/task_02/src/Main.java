package com.addressbookapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Main extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private HashMap<String, Contact> contacts;
    private Contact currentContact;
    private String selectedFilePath;

    public Main() {
        setTitle("AddressBook");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Name");
        JLabel addressLabel = new JLabel("Address:");

        nameField = new JTextField(10);
        addressField = new JTextField(10);
        addressField.setPreferredSize(new Dimension(100, 100));
        addressField.setHorizontalAlignment(JTextField.LEFT);

        topPanel.add(nameLabel);
        topPanel.add(nameField);
        topPanel.add(addressLabel);
        topPanel.add(addressField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton removeButton = new JButton("Remove");
        JButton findButton = new JButton("Find");
        JButton loadButton = new JButton("Load");
        JButton saveButton = new JButton("Save");
        JButton exportButton = new JButton("Export");
        JButton listButton = new JButton("List");

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(removeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(findButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(loadButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(exportButton);
        buttonPanel.add(listButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        contacts = new HashMap<>();
        currentContact = null;

        addButton.addActionListener(e -> addContact());
        editButton.addActionListener(e -> editContact());
        removeButton.addActionListener(e -> removeContact());
        findButton.addActionListener(e -> findContact());
        loadButton.addActionListener(e -> loadContacts());
        saveButton.addActionListener(e -> saveContacts());
        exportButton.addActionListener(e -> exportContact());
        listButton.addActionListener(e -> showContactList());
        prevButton.addActionListener(e -> showPreviousContact());
        nextButton.addActionListener(e -> showNextContact());
    }

    private void addContact() {
        String name = nameField.getText();
        String address = addressField.getText();

        if (name.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name and address.");
            return;
        }

        if (!contacts.containsKey(name)) {
            Contact newContact = new Contact(name, address);
            contacts.put(name, newContact);
            currentContact = newContact;
            JOptionPane.showMessageDialog(this, "\"" + name + "\" has been added to your address book.");
        } else {
            JOptionPane.showMessageDialog(this, "Sorry, \"" + name + "\" is already in your address book.");
        }

        clearFields();
    }

    private void editContact() {
        if (currentContact != null) {
            String name = nameField.getText();
            String address = addressField.getText();

            if (name.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name and address.");
                return;
            }

            if (!name.equals(currentContact.getName())) {
                contacts.remove(currentContact.getName());
            }

            currentContact.setName(name);
            currentContact.setAddress(address);
            contacts.put(name, currentContact);
            JOptionPane.showMessageDialog(this, "\"" + name + "\" has been updated in your address book.");
        } else {
            JOptionPane.showMessageDialog(this, "No contact selected.");
        }
    }

    private void removeContact() {
        if (currentContact != null) {
            String name = currentContact.getName();
            contacts.remove(name);
            clearFields();
            JOptionPane.showMessageDialog(this, "\"" + name + "\" has been removed from your address book.");
            showNextContact();
        } else {
            JOptionPane.showMessageDialog(this, "No contact selected.");
        }
    }

    private void findContact() {
        String searchName = JOptionPane.showInputDialog(this, "Enter the name to find:");
        if (searchName != null && !searchName.isEmpty()) {
            Contact contact = contacts.get(searchName);
            if (contact != null) {
                nameField.setText(contact.getName());
                addressField.setText(contact.getAddress());
                currentContact = contact;
            } else {
                JOptionPane.showMessageDialog(this, "Contact not found for the name: " + searchName);
            }
        }
    }

    private void loadContacts() {
        JFileChooser fileChooser = new JFileChooser();
        int choice = fileChooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedFilePath = selectedFile.getAbsolutePath();
            try (FileReader fileReader = new FileReader(selectedFilePath)) {
                Gson gson = new Gson();
                contacts = gson.fromJson(fileReader, new TypeToken<HashMap<String, Contact>>() {}.getType());
                if (!contacts.isEmpty()) {
                    currentContact = contacts.values().iterator().next();
                    nameField.setText(currentContact.getName());
                    addressField.setText(currentContact.getAddress());
                } else {
                    clearFields();
                }
                JOptionPane.showMessageDialog(this, "Address book loaded from " + selectedFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading address book from " + selectedFilePath);
            }
        }
    }

    private void saveContacts() {
        if (selectedFilePath != null) {
            try (FileWriter fileWriter = new FileWriter(selectedFilePath)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(contacts, fileWriter);
                JOptionPane.showMessageDialog(this, "Address book saved to " + selectedFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving address book to " + selectedFilePath);
            }
        } else {
            JFileChooser fileChooser = new JFileChooser();
            int choice = fileChooser.showSaveDialog(this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                if (!filePath.endsWith(".json")) {
                    filePath += ".json";
                }
                selectedFilePath = filePath;
                saveContacts();
            }
        }
    }

    private void exportContact() {
        if (currentContact != null) {
            String vCardData = "BEGIN:VCARD\n" +
                    "VERSION:3.0\n" +
                    "FN:" + currentContact.getName() + "\n" +
                    "ADR;TYPE=HOME:" + currentContact.getAddress() + "\n" +
                    "END:VCARD";

            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("VCF Files", "vcf");
            fileChooser.setFileFilter(filter);

            int choice = fileChooser.showSaveDialog(this);

            if (choice == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String fileName = selectedFile.getAbsolutePath();

                if (!fileName.endsWith(".vcf")) {
                    fileName += ".vcf";
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    writer.write(vCardData);
                    JOptionPane.showMessageDialog(this, "Contact exported as VCF: " + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error exporting contact as VCF");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to export.");
        }
    }

    private void showContactList() {
        if (contacts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address book is empty.");
            return;
        }

        JFrame contactListFrame = new JFrame("Contact List");
        contactListFrame.setSize(400, 400);

        JTextArea contactListTextArea = new JTextArea();
        contactListTextArea.setEditable(false);

        for (Contact contact : contacts.values()) {
            contactListTextArea.append("Name: " + contact.getName() + "\n");
            contactListTextArea.append("Address: " + contact.getAddress() + "\n\n");
        }

        contactListFrame.add(new JScrollPane(contactListTextArea));
        contactListFrame.setVisible(true);
    }

    private void showNextContact() {
        if (currentContact != null && contacts.size() > 1) {
            String currentName = currentContact.getName();
            boolean found = false;

            for (String name : contacts.keySet()) {
                if (found) {
                    Contact nextContact = contacts.get(name);
                    nameField.setText(nextContact.getName());
                    addressField.setText(nextContact.getAddress());
                    currentContact = nextContact;
                    break;
                }
                if (name.equals(currentName)) {
                    found = true;
                }
            }
        }
    }

    private void showPreviousContact() {
        if (currentContact != null && contacts.size() > 1) {
            String currentName = currentContact.getName();
            String prevName = null;

            for (String name : contacts.keySet()) {
                if (name.equals(currentName)) {
                    break;
                }
                prevName = name;
            }

            if (prevName != null) {
                Contact prevContact = contacts.get(prevName);
                nameField.setText(prevContact.getName());
                addressField.setText(prevContact.getAddress());
                currentContact = prevContact;
            }
        }
    }

    private void clearFields() {
        nameField.setText("");
        addressField.setText("");
    }

    static class Contact {
        private String name;
        private String address;

        public Contact(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main form = new Main();
            form.setVisible(true);
        });
    }
}
