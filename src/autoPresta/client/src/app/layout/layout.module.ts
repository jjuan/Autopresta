import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MatTabsModule } from '@angular/material/tabs';
import { AuthLayoutComponent } from './app-layout/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './app-layout/main-layout/main-layout.component';
import {HeaderComponent} from "./header/header.component";
import {RightSidebarComponent} from "./right-sidebar/right-sidebar.component";
import {SidebarComponent} from "./sidebar/sidebar.component";
import {RouterModule} from "@angular/router";
import {SharedModule} from "../shared/shared.module";

@NgModule({
  imports: [CommonModule, NgbModule, MatTabsModule, RouterModule, SharedModule],
  declarations: [AuthLayoutComponent, MainLayoutComponent, HeaderComponent, RightSidebarComponent, SidebarComponent]
})
export class LayoutModule {}
