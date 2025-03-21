import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem, OrderItem } from './models';
import { RestaurantService } from './restaurant.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  menuItems: MenuItem[] = [];
  selectedItems: OrderItem[] = [];
  totalAmount: number = 0;
  
  constructor(
    private restaurantSvc: RestaurantService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.loadMenuItems();
  }
  
  async loadMenuItems() {
    try {
      this.menuItems = await this.restaurantSvc.getMenuItems();
      // Sort by name in ascending order
      this.menuItems.sort((a, b) => a.name.localeCompare(b.name));
    } catch (error) {
      console.error('Error loading menu items:', error);
    }
  }
  
  addItem(item: MenuItem) {
    const existingItem = this.selectedItems.find(i => i.id === item._id);
    if (existingItem) {
      existingItem.quantity++;
    } else {
      this.selectedItems.push({
        id: item._id,
        name: item.name,
        price: item.price,
        quantity: 1
      });
    }
    this.updateTotal();
  }
  
  removeItem(itemId: string) {
    const index = this.selectedItems.findIndex(i => i.id === itemId);
    if (index >= 0) {
      if (this.selectedItems[index].quantity > 1) {
        this.selectedItems[index].quantity--;
      } else {
        this.selectedItems.splice(index, 1);
      }
      this.updateTotal();
    }
  }
  
  updateTotal() {
    this.totalAmount = this.selectedItems.reduce(
      (sum, item) => sum + (item.price * item.quantity), 0
    );
  }
  
  getSelectedQuantity(itemId: string): number {
    const item = this.selectedItems.find(i => i.id === itemId);
    return item ? item.quantity : 0;
  }
  
  placeOrder() {
    if (this.selectedItems.length > 0) {
      this.router.navigate(['/place-order'], { 
        state: { 
          selectedItems: this.selectedItems,
          totalAmount: this.totalAmount
        } 
      });
    }
  }
} 