export interface SliceResponse<T> {
  items: T[];
  hasNext: boolean;
  page: number;
  size: number;
  sort: string;
}

