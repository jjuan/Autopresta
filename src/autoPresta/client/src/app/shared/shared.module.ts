import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerModule } from 'ngx-spinner';

import { MaterialModule } from './material.module';
import {PerfectScrollbarModule} from "ngx-perfect-scrollbar";
import {OwlDateTimeModule} from "ng-pick-datetime";
import {CustomFormsModule} from "ngx-custom-validators";
import {NgxDatatableModule} from "@swimlane/ngx-datatable";

const modules = [
  CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgbModule, NgxSpinnerModule, PerfectScrollbarModule,
  OwlDateTimeModule, NgxDatatableModule, CustomFormsModule
];
@NgModule({
  declarations: [],
  imports: [modules],
  exports: [ modules, MaterialModule ]
})
export class SharedModule {}
