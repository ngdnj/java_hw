public final class ComplexMatrix {
    private static final double EPSILON = 1e-12;

    private final int rows;
    private final int columns;
    private final Complex[][] data;

    public ComplexMatrix(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Размеры матрицы должны быть положительными.");
        }
        this.rows = rows;
        this.columns = columns;
        data = new Complex[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                data[i][j] = Complex.ZERO;
            }
        }
    }

    public ComplexMatrix(Complex[][] values) {
        if (values == null || values.length == 0
                || values[0] == null || values[0].length == 0) {
            throw new IllegalArgumentException("Матрица не должна быть пустой.");
        }
        rows = values.length;
        columns = values[0].length;
        data = new Complex[rows][columns];
        for (int i = 0; i < rows; i++) {
            if (values[i] == null || values[i].length != columns) {
                throw new IllegalArgumentException("Все строки должны иметь одинаковую длину.");
            }
            for (int j = 0; j < columns; j++) {
                if (values[i][j] == null) {
                    throw new IllegalArgumentException("Элемент матрицы не может быть null.");
                }
                data[i][j] = values[i][j];
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Complex get(int row, int column) {
        checkIndex(row, column);
        return data[row][column];
    }

    public void set(int row, int column, Complex value) {
        checkIndex(row, column);
        if (value == null) {
            throw new IllegalArgumentException("Элемент матрицы не может быть null.");
        }
        data[row][column] = value;
    }

    public ComplexMatrix add(ComplexMatrix other) {
        checkOther(other);
        if (rows != other.rows || columns != other.columns) {
            throw new IllegalArgumentException("Для сложения размеры матриц должны совпадать.");
        }
        ComplexMatrix result = new ComplexMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.data[i][j] = data[i][j].add(other.data[i][j]);
            }
        }
        return result;
    }

    public ComplexMatrix multiply(ComplexMatrix other) {
        checkOther(other);
        if (columns != other.rows) {
            throw new IllegalArgumentException(
                    "Для умножения число столбцов A должно совпадать с числом строк B."
            );
        }
        ComplexMatrix result = new ComplexMatrix(rows, other.columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < other.columns; j++) {
                Complex sum = Complex.ZERO;
                for (int k = 0; k < columns; k++) {
                    sum = sum.add(data[i][k].multiply(other.data[k][j]));
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    public ComplexMatrix divide(ComplexMatrix other) {
        checkOther(other);
        if (other.rows != other.columns) {
            throw new IllegalArgumentException("Для деления матрица B должна быть квадратной.");
        }
        if (columns != other.rows) {
            throw new IllegalArgumentException(
                    "Для деления число столбцов A должно совпадать с размером матрицы B."
            );
        }
        return multiply(other.inverse());
    }

    public ComplexMatrix transpose() {
        ComplexMatrix result = new ComplexMatrix(columns, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.data[j][i] = data[i][j];
            }
        }
        return result;
    }

    public Complex determinant() {
        checkSquare();
        Complex[][] values = copyData();
        double[] scales = rowScales();
        Complex result = Complex.ONE;
        for (int column = 0; column < columns; column++) {
            int pivotRow = findPivotRow(values, scales, column);
            if (isZeroPivot(values[pivotRow][column], scales[pivotRow])) {
                return Complex.ZERO;
            }
            if (pivotRow != column) {
                swapRows(values, scales, column, pivotRow);
                result = result.negate();
            }
            Complex pivot = values[column][column];
            result = result.multiply(pivot);
            for (int row = column + 1; row < rows; row++) {
                Complex factor = values[row][column].divide(pivot);
                values[row][column] = Complex.ZERO;
                for (int j = column + 1; j < columns; j++) {
                    values[row][j] = values[row][j].subtract(
                            factor.multiply(values[column][j])
                    );
                }
            }
        }
        return result;
    }

    public ComplexMatrix inverse() {
        checkSquare();
        Complex[][] values = new Complex[rows][2 * columns];
        double[] scales = rowScales();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                values[i][j] = data[i][j];
                values[i][j + columns] = i == j ? Complex.ONE : Complex.ZERO;
            }
        }
        for (int column = 0; column < columns; column++) {
            int pivotRow = findPivotRow(values, scales, column);
            if (isZeroPivot(values[pivotRow][column], scales[pivotRow])) {
                throw new ArithmeticException(
                        "Матрица вырождена или близка к вырожденной. Обратную матрицу вычислить нельзя."
                );
            }
            swapRows(values, scales, column, pivotRow);
            Complex pivot = values[column][column];
            for (int j = column + 1; j < 2 * columns; j++) {
                values[column][j] = values[column][j].divide(pivot);
            }
            values[column][column] = Complex.ONE;
            for (int row = 0; row < rows; row++) {
                if (row == column) {
                    continue;
                }
                Complex factor = values[row][column];
                values[row][column] = Complex.ZERO;
                for (int j = column + 1; j < 2 * columns; j++) {
                    values[row][j] = values[row][j].subtract(
                            factor.multiply(values[column][j])
                    );
                }
            }
        }
        ComplexMatrix result = new ComplexMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.data[i][j] = values[i][j + columns];
            }
        }
        return result;
    }

    private void checkIndex(int row, int column) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            throw new IndexOutOfBoundsException("Индекс выходит за границы матрицы.");
        }
    }

    private static void checkOther(ComplexMatrix other) {
        if (other == null) {
            throw new IllegalArgumentException("Матрица не может быть null.");
        }
    }

    private void checkSquare() {
        if (rows != columns) {
            throw new IllegalArgumentException("Операция определена только для квадратной матрицы.");
        }
    }

    private Complex[][] copyData() {
        Complex[][] copy = new Complex[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                copy[i][j] = data[i][j];
            }
        }
        return copy;
    }

    private double[] rowScales() {
        double[] scales = new double[rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                scales[i] = Math.max(scales[i], data[i][j].abs());
            }
        }
        return scales;
    }

    private static int findPivotRow(Complex[][] values, double[] scales, int column) {
        int pivotRow = column;
        double bestRatio = -1.0;
        for (int row = column; row < values.length; row++) {
            double ratio = scales[row] == 0.0 ? 0.0 : values[row][column].abs() / scales[row];
            if (ratio > bestRatio) {
                bestRatio = ratio;
                pivotRow = row;
            }
        }
        return pivotRow;
    }

    private static boolean isZeroPivot(Complex pivot, double scale) {
        return scale == 0.0 || pivot.abs() / scale <= EPSILON;
    }

    private static void swapRows(Complex[][] values, double[] scales, int first, int second) {
        Complex[] row = values[first];
        values[first] = values[second];
        values[second] = row;
        double scale = scales[first];
        scales[first] = scales[second];
        scales[second] = scale;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            if (i > 0) {
                builder.append(System.lineSeparator());
            }
            builder.append("[ ");
            for (int j = 0; j < columns; j++) {
                if (j > 0) {
                    builder.append("  ");
                }
                builder.append(data[i][j]);
            }
            builder.append(" ]");
        }
        return builder.toString();
    }
}
