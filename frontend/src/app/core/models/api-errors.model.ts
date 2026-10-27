/**
 * Standardized API Error Response representation.
 */
export interface ApiError {
  errorCode: string;
  message: string;
  status: number;
  timestamp?: string;
  details?: string[];
}
