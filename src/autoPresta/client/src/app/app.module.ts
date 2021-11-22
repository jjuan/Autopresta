import { NgModule } from '@angular/core';
import { CoreModule } from './core/core.module';
import { SharedModule } from './shared/shared.module';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PageLoaderComponent } from './layout/page-loader/page-loader.component';

import { LocationStrategy, HashLocationStrategy } from '@angular/common';
import { NgxSpinnerModule } from 'ngx-spinner';
import { PerfectScrollbarModule, PERFECT_SCROLLBAR_CONFIG, PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';
import { ClickOutsideModule } from 'ng-click-outside';
import { HttpClientModule, HTTP_INTERCEPTORS, } from '@angular/common/http';
import { WINDOW_PROVIDERS } from './core/service/window.service';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {ReactiveFormsModule} from "@angular/forms";
import {NgxMaskModule} from "ngx-mask";
import {LayoutModule} from "./layout/layout.module";
import {ChartsModule} from "@progress/kendo-angular-charts";
import {OwlNativeDateTimeModule} from "ng-pick-datetime";
import {environment} from "../environments/environment";
import {HttpCalIInterceptor} from "./core/interceptor/http.interceptor";
import {DynamicScriptLoaderService} from "./core/service/dynamic-script-loader.service";
import {RightSidebarService} from "./core/service/rightsidebar.service";
import {RestService} from "./core/service/rest.service";
import {GlobalService} from "./core/service/global.service";
import {ConfigService} from "./config/config.service";
const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = { suppressScrollX: true, wheelPropagation: false };

@NgModule({
  declarations: [ AppComponent, PageLoaderComponent, ],
  imports: [
    BrowserModule,BrowserAnimationsModule,AppRoutingModule,HttpClientModule,PerfectScrollbarModule,NgxSpinnerModule,
    CoreModule, ClickOutsideModule, SharedModule, NgbModule, ReactiveFormsModule, NgxMaskModule.forRoot(), LayoutModule,
    SharedModule, OwlNativeDateTimeModule, ChartsModule
  ],
  providers: [
    { provide: LocationStrategy, useClass: HashLocationStrategy }, { provide: 'API_URL', useValue: environment.serverUrl},
    { provide: HTTP_INTERCEPTORS, useClass: HttpCalIInterceptor, multi: true },
    { provide: PERFECT_SCROLLBAR_CONFIG, useValue: DEFAULT_PERFECT_SCROLLBAR_CONFIG },
    DynamicScriptLoaderService, RightSidebarService, RestService, GlobalService, ConfigService, WINDOW_PROVIDERS
  ],
  entryComponents: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
