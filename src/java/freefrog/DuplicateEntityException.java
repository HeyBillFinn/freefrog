package freefrog;

public class DuplicateEntityException extends RuntimeException {
  public DuplicateEntityException(String message) {
    super(message);
  }
}
