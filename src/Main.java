import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final int MAX_SIZE = 100;

    private final Scanner scanner = new Scanner(System.in);
    private ComplexMatrix matrixA;
    private ComplexMatrix matrixB;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("Матрицы комплексных чисел");
        try {
            boolean running = true;
            while (running) {
                printMenu();
                int choice = readInt("Ваш выбор: ", 0, 8);
                try {
                    switch (choice) {
                        case 1:
                            matrixA = readMatrix("A");
                            System.out.println("Матрица A создана:");
                            System.out.println(matrixA);
                            break;
                        case 2:
                            matrixB = readMatrix("B");
                            System.out.println("Матрица B создана:");
                            System.out.println(matrixB);
                            break;
                        case 3:
                            printMatrix(matrixA, "A");
                            printMatrix(matrixB, "B");
                            break;
                        case 4:
                            printResult(requireMatrix(matrixA, "A").add(requireMatrix(matrixB, "B")));
                            break;
                        case 5:
                            printResult(requireMatrix(matrixA, "A").multiply(requireMatrix(matrixB, "B")));
                            break;
                        case 6:
                            printResult(requireMatrix(matrixA, "A").divide(requireMatrix(matrixB, "B")));
                            break;
                        case 7:
                            printResult(selectMatrix().transpose());
                            break;
                        case 8:
                            System.out.println("Определитель: " + selectMatrix().determinant());
                            break;
                        case 0:
                            running = false;
                            System.out.println("Работа завершена.");
                            break;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                } catch (ArithmeticException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Ввод завершён. Программа закрыта.");
        } finally {
            scanner.close();
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Создать или заменить матрицу A");
        System.out.println("2. Создать или заменить матрицу B");
        System.out.println("3. Показать матрицы");
        System.out.println("4. Сложить A + B");
        System.out.println("5. Умножить A * B");
        System.out.println("6. Разделить A / B = A * B^(-1)");
        System.out.println("7. Транспонировать матрицу");
        System.out.println("8. Вычислить определитель");
        System.out.println("0. Выход");
    }

    private ComplexMatrix readMatrix(String name) {
        System.out.println("Создание матрицы " + name);
        int rows = readInt("Количество строк (1-" + MAX_SIZE + "): ", 1, MAX_SIZE);
        int columns = readInt("Количество столбцов (1-" + MAX_SIZE + "): ", 1, MAX_SIZE);
        ComplexMatrix matrix = new ComplexMatrix(rows, columns);
        System.out.println("Для каждого элемента введите два числа через пробел: действительную и мнимую части.");
        System.out.println("Например, 2 -3 означает 2 - 3i. Каждый элемент вводится в отдельной строке.");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix.set(i, j, readComplex(name + "[" + (i + 1) + "][" + (j + 1) + "]: "));
            }
        }
        return matrix;
    }

    private Complex readComplex(String prompt) {
        while (true) {
            String[] parts = readLine(prompt).replace(',', '.').split("\\s+");
            if (parts.length == 2) {
                try {
                    return new Complex(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                } catch (IllegalArgumentException e) {
                    System.out.println("Введите два конечных числа через пробел, например: 1.5 -2.");
                }
            } else {
                System.out.println("Введите ровно два числа через пробел, без буквы i.");
            }
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(readLine(prompt));
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Требуется целое число.");
            }
            System.out.println("Введите число от " + min + " до " + max + ".");
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new NoSuchElementException("Ввод завершён.");
        }
        return scanner.nextLine().trim();
    }

    private ComplexMatrix selectMatrix() {
        int choice = readInt("Выберите матрицу (1 - A, 2 - B): ", 1, 2);
        if (choice == 1) {
            return requireMatrix(matrixA, "A");
        }
        return requireMatrix(matrixB, "B");
    }

    private ComplexMatrix requireMatrix(ComplexMatrix matrix, String name) {
        if (matrix == null) {
            throw new IllegalArgumentException("Сначала создайте матрицу " + name + ".");
        }
        return matrix;
    }

    private void printMatrix(ComplexMatrix matrix, String name) {
        if (matrix == null) {
            System.out.println("Матрица " + name + " не создана.");
        } else {
            System.out.println("Матрица " + name + " (" + matrix.getRows() + " x " + matrix.getColumns() + "):");
            System.out.println(matrix);
        }
    }

    private void printResult(ComplexMatrix result) {
        System.out.println("Результат:");
        System.out.println(result);
    }
}
