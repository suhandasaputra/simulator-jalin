package main;

public class main {

    public static void main(String[] args) {
        try {

            // Create an instance of ClientCLI
            MainGUI mainGUI = new MainGUI();
            
            java.awt.EventQueue.invokeLater(() -> {
                mainGUI.setVisible(true);
                mainGUI.setjTextFieldAs(mainGUI.getSelectedMode());
                mainGUI.setserver();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
