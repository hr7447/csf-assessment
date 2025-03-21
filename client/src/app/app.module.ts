import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { MenuComponent } from './components/menu.component';
import { PlaceOrderComponent } from './components/place-order.component';
import { ConfirmationComponent } from './components/confirmation.component';
import { RestaurantService } from './restaurant.service';

@NgModule({
  declarations: [
    AppComponent, MenuComponent, PlaceOrderComponent, ConfirmationComponent
  ],
  imports: [
    BrowserModule, 
    ReactiveFormsModule, 
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [RestaurantService],
  bootstrap: [AppComponent]
})
export class AppModule { }