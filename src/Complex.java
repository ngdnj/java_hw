public final class Complex {
    public static final Complex ZERO = new Complex(0.0, 0.0);
    public static final Complex ONE = new Complex(1.0, 0.0);

    private final double real;
    private final double imaginary;

    public Complex(double real, double imaginary) {
        if (!Double.isFinite(real) || !Double.isFinite(imaginary)) {
            throw new IllegalArgumentException("Части комплексного числа должны быть конечными.");
        }
        this.real = real == 0.0 ? 0.0 : real;
        this.imaginary = imaginary == 0.0 ? 0.0 : imaginary;
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public Complex add(Complex other) {
        return new Complex(real + other.real, imaginary + other.imaginary);
    }

    public Complex subtract(Complex other) {
        return new Complex(real - other.real, imaginary - other.imaginary);
    }

    public Complex multiply(Complex other) {
        return new Complex(
                real * other.real - imaginary * other.imaginary,
                real * other.imaginary + imaginary * other.real
        );
    }

    public Complex divide(Complex other) {
        double scale = Math.max(Math.abs(other.real), Math.abs(other.imaginary));
        if (scale == 0.0) {
            throw new ArithmeticException("Деление на нулевое комплексное число.");
        }
        double c = other.real / scale;
        double d = other.imaginary / scale;
        double a = real / scale;
        double b = imaginary / scale;
        double denominator = c * c + d * d;
        return new Complex(
                (a * c + b * d) / denominator,
                (b * c - a * d) / denominator
        );
    }

    public Complex negate() {
        return new Complex(-real, -imaginary);
    }

    public double abs() {
        return Math.hypot(real, imaginary);
    }

    @Override
    public String toString() {
        if (imaginary == 0.0) {
            return Double.toString(real);
        }
        if (real == 0.0) {
            return imaginary + "i";
        }
        if (imaginary < 0.0) {
            return real + " - " + Math.abs(imaginary) + "i";
        }
        return real + " + " + imaginary + "i";
    }
}
