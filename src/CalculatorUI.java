import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.MathContext;

public class CalculatorUI extends JFrame {
    private final JTextField display = new JTextField();
    private final JLabel historyLabel = new JLabel(" ", SwingConstants.RIGHT);
    private boolean degrees = true;
    private JButton modeButton;

    private static final String[] BUTTONS = {
        "Deg", "sin", "cos", "tan", "C",
        "asin", "acos", "atan", "π", "⌫",
        "log", "ln", "√", "e", "^",
        "(", "7", "8", "9", "/",
        ")", "4", "5", "6", "*",
        "!", "1", "2", "3", "-",
        "abs", "0", ".", "=", "+"
    };

    public CalculatorUI() {
        super("Scientific Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Top: history + display
        display.setFont(new Font("SansSerif", Font.BOLD, 28));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.addActionListener(e -> calculate()); // Enter key = "="
        historyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        historyLabel.setForeground(Color.GRAY);

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        top.add(historyLabel, BorderLayout.NORTH);
        top.add(display, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Buttons
        JPanel grid = new JPanel(new GridLayout(7, 5, 5, 5));
        grid.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        for (String label : BUTTONS) {
            JButton b = new JButton(label);
            b.setFont(new Font("SansSerif", Font.BOLD, 16));
            b.setFocusPainted(false);
            if (label.equals("Deg")) modeButton = b;
            if (label.equals("=")) b.setBackground(new Color(76, 175, 80));
            if (label.equals("C") || label.equals("⌫")) b.setBackground(new Color(239, 154, 154));
            b.addActionListener(e -> handle(label));
            grid.add(b);
        }
        add(grid, BorderLayout.CENTER);

        setSize(420, 560);
        setLocationRelativeTo(null);
    }

    private void handle(String label) {
        switch (label) {
            case "C":   display.setText(""); historyLabel.setText(" "); break;
            case "⌫":  backspace(); break;
            case "=":   calculate(); break;
            case "Deg":
            case "Rad": toggleMode(); break;
            case "π":   append("pi"); break;
            case "√":   append("sqrt("); break;
            case "sin": case "cos": case "tan":
            case "asin": case "acos": case "atan":
            case "log": case "ln": case "abs":
                append(label + "(");
                break;
            default:    append(label);
        }
        display.requestFocusInWindow();
    }

    private void append(String text) {
        display.setText(display.getText() + text);
    }

    private void backspace() {
        String t = display.getText();
        if (!t.isEmpty()) display.setText(t.substring(0, t.length() - 1));
    }

    private void toggleMode() {
        degrees = !degrees;
        modeButton.setText(degrees ? "Deg" : "Rad");
    }

    private void calculate() {
        String expr = display.getText().trim();
        if (expr.isEmpty()) return;
        try {
            double value = new Evaluator(expr, degrees).evaluate();
            String result = format(value);
            historyLabel.setText(expr + " =");
            display.setText(result);
        } catch (Exception ex) {
            historyLabel.setText("Error: " + ex.getMessage());
        }
    }

    private String format(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            throw new ArithmeticException("Math error");
        }
        if (Math.abs(v) < 1e-12) return "0"; // cleans up sin(180) = 1.2e-16
        if (v == Math.rint(v) && Math.abs(v) < 1e15) return String.valueOf((long) v);
        return new BigDecimal(v).round(new MathContext(12))
                .stripTrailingZeros().toPlainString();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new CalculatorUI().setVisible(true));
    }
}