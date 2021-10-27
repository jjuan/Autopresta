import { NgModule } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MatRippleModule } from '@angular/material/core';
import { NgxMaskModule } from 'ngx-mask';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatListModule } from '@angular/material/list';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import {MatFormFieldModule, MatLabel} from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule} from "@angular/material/snack-bar";
import { MatTableModule } from "@angular/material/table";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatMenuModule } from "@angular/material/menu";
import { MaterialFileInputModule } from "ngx-material-file-input";
import { MatSortModule } from "@angular/material/sort";
import { MatSelectModule } from "@angular/material/select";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCardModule } from "@angular/material/card";
import { MatRadioModule } from "@angular/material/radio";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { AutocompleteLibModule } from "angular-ng-autocomplete";
import { MatProgressButtonsModule } from "mat-progress-buttons";
import { MatTabsModule } from "@angular/material/tabs";
import { MatStepperModule } from "@angular/material/stepper";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";

const materialModules = [
  MatButtonModule, MatInputModule, MatListModule, MatIconModule, MatTooltipModule, MatDatepickerModule,
  MatNativeDateModule, NgxMaskModule.forRoot(), MatButtonToggleModule, MatFormFieldModule, MatSnackBarModule,
  MatNativeDateModule, MatTableModule, MatPaginatorModule, MatRadioModule, MatSelectModule, MatCheckboxModule,
  MatCardModule, MatDialogModule, MatSortModule, MatToolbarModule, MaterialFileInputModule, MatMenuModule,
  MatSlideToggleModule,MatAutocompleteModule, MatTabsModule, MatProgressButtonsModule, AutocompleteLibModule,
  MatRippleModule, MatStepperModule, MatProgressSpinnerModule
];

@NgModule({
  declarations: [],
  imports: [ materialModules ],
  exports: [ materialModules ],
})
export class MaterialModule {}
