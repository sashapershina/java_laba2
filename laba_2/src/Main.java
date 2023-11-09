import java.util.Scanner;

class ExpressionEvaluator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите выражение:");
        String expression = scanner.nextLine();

        // Получение значения переменных от пользователя
        expression = getVariableValues(expression, scanner);

        try {
            // Вычисление значения выражения
            double result = evaluateExpression(expression);
            System.out.println("Результат: " + result);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public static String getVariableValues(String expression, Scanner scanner) {
        String[] parts = expression.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.matches("[a-zA-Z]+")) {
                System.out.print("Введите значение для переменной " + part + ": ");
                double value = scanner.nextDouble();
                expression = expression.replace(part, String.valueOf(value));
            }
        }
        return expression;
    }

    public static double evaluateExpression(String expression) {
        // Удаление пробелов из выражения
        expression = expression.replaceAll("\\s+", "");

        // Проверка на корректное количество открывающих и закрывающих скобок
        if (countOccurrences(expression, '(') != countOccurrences(expression, ')')) {
            throw new IllegalArgumentException("Некорректное количество скобок");
        }

        // Вычисление значения выражения
        return evaluateExpressionRecursively(expression);
    }

    private static double evaluateExpressionRecursively(String expression) {
        // Проверка наличия и вычисление вложенных выражений в скобках
        if (expression.contains("(")) {
            int startIndex = expression.lastIndexOf("(");
            int endIndex = expression.indexOf(")", startIndex);
            String nestedExpression = expression.substring(startIndex + 1, endIndex);
            double nestedResult = evaluateExpressionRecursively(nestedExpression);
            expression = expression.replace("(" + nestedExpression + ")", String.valueOf(nestedResult));
        }

        // Проверка наличия и вычисление функций
        if (expression.contains("sin")) {
            int startIndex = expression.indexOf("sin") + 3;
            int endIndex = expression.indexOf(")", startIndex);
            String parameter = expression.substring(startIndex + 1, endIndex);
            double parameterValue = Double.parseDouble(parameter);
            double functionResult = Math.sin(Math.toRadians(parameterValue));
            expression = expression.replace("sin(" + parameter + ")", String.valueOf(functionResult));
        }
        if (expression.contains("cos")) {
            int startIndex = expression.indexOf("cos") + 3;
            int endIndex = expression.indexOf(")", startIndex);
            String parameter = expression.substring(startIndex + 1, endIndex);
            double parameterValue = Double.parseDouble(parameter);
            double functionResult = Math.cos(Math.toRadians(parameterValue));
            expression = expression.replace("cos(" + parameter + ")", String.valueOf(functionResult));
        }

        // Вычисление значения выражения без вложенных выражений и функций
        return evaluateSimpleExpression(expression);
    }

    private static double evaluateSimpleExpression(String expression) {
        // Проверка на наличие недопустимых символов
        if (expression.matches(".*[^0-9.+-/*()].*")) {
            throw new IllegalArgumentException("Выражение содержит недопустимые символы");
        }

        // Вычисление умножения и деления
        while (expression.contains("*") || expression.contains("/")) {
            int multiplicationIndex = expression.indexOf("*");
            int divisionIndex = expression.indexOf("/");
            int operatorIndex = -1;
            if (multiplicationIndex != -1 && divisionIndex != -1) {
                if (multiplicationIndex < divisionIndex) {
                    operatorIndex = multiplicationIndex;
                } else {
                    operatorIndex = divisionIndex;
                }
            } else if (multiplicationIndex != -1) {
                operatorIndex = multiplicationIndex;
            } else if (divisionIndex != -1) {
                operatorIndex = divisionIndex;
            } else {
                break;
            }
            double leftOperand = getLeftOperand(expression, operatorIndex);
            double rightOperand = getRightOperand(expression, operatorIndex);
            double operationResult;
            if (expression.charAt(operatorIndex) == '*') {
                operationResult = leftOperand * rightOperand;
            } else {
                operationResult = leftOperand / rightOperand;
            }
            expression = replaceOperationWithResult(expression, operatorIndex, operationResult);
        }

        // Вычисление сложения и вычитания
        while (expression.contains("+") || expression.contains("-")) {
            int additionIndex = expression.indexOf("+");
            int subtractionIndex = expression.indexOf("-");
            int operatorIndex = -1;
            if (additionIndex != -1 && subtractionIndex != -1) {
                if (additionIndex < subtractionIndex) {
                    operatorIndex = additionIndex;
                } else {
                    operatorIndex = subtractionIndex;
                }
            } else if (additionIndex != -1) {
                operatorIndex = additionIndex;
            } else if (subtractionIndex != -1) {
                operatorIndex = subtractionIndex;
            } else {
                break;
            }
            double leftOperand = getLeftOperand(expression, operatorIndex);
            double rightOperand = getRightOperand(expression, operatorIndex);
            double operationResult;
            if (expression.charAt(operatorIndex) == '+') {
                operationResult = leftOperand + rightOperand;
            } else {
                operationResult = leftOperand - rightOperand;
            }
            expression = replaceOperationWithResult(expression, operatorIndex, operationResult);
        }

        // Проверка на наличие единственного числа в выражении
        if (!expression.matches("-?[0-9.]+")) {
            throw new IllegalArgumentException("Выражение записано некорректно");
        }

        // Возврат значения выражения
        return Double.parseDouble(expression);
    }

    private static double getLeftOperand(String expression, int operatorIndex) {
        int endIndex = operatorIndex;
        while (endIndex > 0 && "+-*/".indexOf(expression.charAt(endIndex - 1)) == -1) {
            endIndex--;
        }
        String leftOperandString = expression.substring(endIndex, operatorIndex);
        return Double.parseDouble(leftOperandString);
    }

    private static double getRightOperand(String expression, int operatorIndex) {
        int startIndex = operatorIndex + 1;
        while (startIndex < expression.length() && "+-*/".indexOf(expression.charAt(startIndex)) == -1) {
            startIndex++;
        }
        String rightOperandString = expression.substring(operatorIndex + 1, startIndex);
        return Double.parseDouble(rightOperandString);
    }

    private static String replaceOperationWithResult(String expression, int operatorIndex, double result) {
        int startIndex = operatorIndex;
        while (startIndex > 0 && "+-*/".indexOf(expression.charAt(startIndex - 1)) == -1) {
            startIndex--;
        }
        int endIndex = operatorIndex + 1;
        while (endIndex < expression.length() && "+-*/".indexOf(expression.charAt(endIndex)) == -1) {
            endIndex++;
        }
        String operationString = expression.substring(startIndex, endIndex);
        return expression.replace(operationString, String.valueOf(result));
    }

    private static int countOccurrences(String text, char target) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }
}