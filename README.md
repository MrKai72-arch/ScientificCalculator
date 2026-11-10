# Scientific Calculator (Java)

A scientific calculator built with pure Java (Swing GUI, no external libraries).

## Features
- Basic operations: + - * / ^ and parentheses
- Trigonometry: sin, cos, tan, asin, acos, atan (Degrees/Radians toggle)
- log, ln, sqrt, abs, factorial (!)
- Constants: pi, e
- Correct operator precedence via a recursive descent parser

## Run
cd src
javac Evaluator.java CalculatorUI.java
java CalculatorUI

## Structure
- `Evaluator.java` – expression parser and math engine
- `CalculatorUI.java` – Swing user interface