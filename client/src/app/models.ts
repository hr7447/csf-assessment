// You may use this file to create any models

export interface MenuItem {
  _id: string;
  name: string;
  price: number;
  description: string;
}

export interface OrderItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
}

export interface Order {
  username: string;
  password: string;
  items: {
    id: string;
    price: number;
    quantity: number;
  }[];
}

export interface OrderConfirmation {
  orderId: string;
  paymentId: string;
  total: number;
  timestamp: number;
}
