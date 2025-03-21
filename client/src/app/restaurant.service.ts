import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, lastValueFrom } from 'rxjs';
import { MenuItem, Order, OrderConfirmation } from './models';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {

  private API_URL = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) { }

  // TODO: Task 2.2
  // You change the method's signature but not the name
  getMenuItems(): Promise<MenuItem[]> {
    return lastValueFrom(
      this.http.get<MenuItem[]>(`${this.API_URL}/menu`)
    );
  }

  // TODO: Task 3.2
  placeOrder(order: Order): Promise<OrderConfirmation> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    return lastValueFrom(
      this.http.post<OrderConfirmation>(`${this.API_URL}/food_order`, order, { headers })
    );
  }
}
