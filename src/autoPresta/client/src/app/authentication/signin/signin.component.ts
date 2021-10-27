import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {GlobalService} from "../../core/service/global.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {HttpClient} from "@angular/common/http";
import {AuthGuard} from "../../core/guard/auth.guard";
import {MatProgressBar} from "@angular/material/progress-bar";
import {MatButton} from "@angular/material/button";
import {DialogService} from "../../core/service/dialog.service";
@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.scss']
})
export class SigninComponent implements OnInit {
  @ViewChild(MatProgressBar, {static: false}) progressBar: MatProgressBar;
  @ViewChild(MatButton, {static: false}) submitButton: MatButton;
  loginForm: FormGroup;
  submitted = false;
  returnUrl: string;
  error = '';
  hide = true;
  constructor(
    private formBuilder: FormBuilder, private route: ActivatedRoute, private router: Router, private http: HttpClient,
    private globalService: GlobalService, private snack: MatSnackBar, private authGuard: AuthGuard, private dialogService: DialogService
  ) {}
  ngOnInit() {
    const validate = this.validateSession();
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
    validate.subscribe( data => {
      const c = data as any;
      this.authGuard.saveCredentials(c.role, c.puesto, c.avatar, c.usuario, c.menu);
      this.router.navigate(['Procesos/Contrato']);
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    const signinData = this.loginForm.value
    this.submitButton.disabled = true;
    this.http.post(this.globalService.BASE_API_URL + 'api/login', signinData).subscribe(data => {
      this.submitButton.disabled = false;
      const d = data as any;
      let options;
      this.authGuard.autheticate(d.access_token, signinData.username);
      options = this.globalService.getHttpOptions();
      options.params = {id: signinData.username};
      this.http.get(this.globalService.BASE_API_URL + 'init/getsession', options).subscribe(next => {
        const c = next as any;
        this.authGuard.saveCredentials(c.role, c.puesto, c.avatar, c.usuario, c.menu);
        this.router.navigate(['Procesos/Contrato']);
      });
    }, error => {
      this.submitButton.disabled = false;
      if ( error.error.mensaje !== undefined) {
        this.dialogService.snack('danger', error.error.mensaje == 'Sorry, you have exceeded your maximum number of open sessions.' ?
          'Lo sentimos, Usted ha excedido el numero maximo de sesiones':'');
      }
    });
  }

  validateSession() {
    return this.http.get(this.globalService.BASE_API_URL + 'init/getsession', this.globalService.getHttpOptions());
  }
}
