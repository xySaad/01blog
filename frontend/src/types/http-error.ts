class HttpError extends Error {
  constructor(
    public status: number,
    message?: string,
  ) {
    super(message ?? `HTTP ${status}`);
  }
}
