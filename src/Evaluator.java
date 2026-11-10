public class Evaluator {
    private final String input;
    private final boolean degrees;
    private int pos = 0;

    public Evaluator(String input, boolean degrees) {
        this.input = input.replaceAll("\\s+", "");
        this.degrees = degrees;
    }

    public double evaluate() {
        double result = parseExpression();
        if (pos < input.length()) {
            throw new IllegalArgumentException("Unexpected '" + input.charAt(pos) + "'");
        }
        return result;
    }

    private boolean match(char c) {
        if (pos < input.length() && input.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    // expression = term { (+|-) term }
    private double parseExpression() {
        double value = parseTerm();
        while (true) {
            if (match('+')) value += parseTerm();
            else if (match('-')) value -= parseTerm();
            else return value;
        }
    }

    // term = factor { (*|/) factor }
    private double parseTerm() {
        double value = parseFactor();
        while (true) {
            if (match('*')) {
                value *= parseFactor();
            } else if (match('/')) {
                double divisor = parseFactor();
                if (divisor == 0) throw new ArithmeticException("Divide by zero");
                value /= divisor;
            } else {
                return value;
            }
        }
    }

    // factor = [+|-] postfix [ ^ factor ]   (power is right-associative)
    private double parseFactor() {
        if (match('-')) return -parseFactor();
        if (match('+')) return parseFactor();
        double base = parsePostfix();
        if (match('^')) return Math.pow(base, parseFactor());
        return base;
    }

    // postfix = primary { ! }
    private double parsePostfix() {
        double value = parsePrimary();
        while (match('!')) {
            value = factorial(value);
        }
        return value;
    }

    // primary = number | ( expression ) | function(expression) | constant
    private double parsePrimary() {
        if (match('(')) {
            double value = parseExpression();
            if (!match(')')) throw new IllegalArgumentException("Missing )");
            return value;
        }

        int start = pos;
        if (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            while (pos < input.length()
                    && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                pos++;
            }
            return Double.parseDouble(input.substring(start, pos));
        }

        if (pos < input.length() && Character.isLetter(input.charAt(pos))) {
            while (pos < input.length() && Character.isLetter(input.charAt(pos))) {
                pos++;
            }
            String name = input.substring(start, pos);

            if (name.equals("pi")) return Math.PI;
            if (name.equals("e")) return Math.E;

            if (!match('(')) throw new IllegalArgumentException("Expected ( after " + name);
            double arg = parseExpression();
            if (!match(')')) throw new IllegalArgumentException("Missing )");
            return applyFunction(name, arg);
        }

        throw new IllegalArgumentException("Invalid expression");
    }

    private double applyFunction(String name, double x) {
        switch (name) {
            case "sin":  return Math.sin(toRadians(x));
            case "cos":  return Math.cos(toRadians(x));
            case "tan":  return Math.tan(toRadians(x));
            case "asin": return fromRadians(Math.asin(x));
            case "acos": return fromRadians(Math.acos(x));
            case "atan": return fromRadians(Math.atan(x));
            case "log":  return Math.log10(x);
            case "ln":   return Math.log(x);
            case "sqrt":
                if (x < 0) throw new ArithmeticException("Invalid input for sqrt");
                return Math.sqrt(x);
            case "abs":  return Math.abs(x);
            default: throw new IllegalArgumentException("Unknown function: " + name);
        }
    }

    private double toRadians(double x) {
        return degrees ? Math.toRadians(x) : x;
    }

    private double fromRadians(double x) {
        return degrees ? Math.toDegrees(x) : x;
    }

    private double factorial(double n) {
        if (n < 0 || n != Math.floor(n) || n > 170) {
            throw new ArithmeticException("Factorial needs integer 0-170");
        }
        double result = 1;
        for (int i = 2; i <= (int) n; i++) result *= i;
        return result;
    }
}