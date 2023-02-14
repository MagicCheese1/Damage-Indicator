package io.github.magiccheese1.damageindicator.exceptions;

/**
 * The nms access exception is a runtime exception that should be thrown if access to the server internals, through
 * either reflection or remapping,
 * fails due to an unexpected ABI.
 */
public class NMSAccessException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     *
     * @since 1.4
     */
    public NMSAccessException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     *
     * @since 1.4
     */
    public NMSAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
