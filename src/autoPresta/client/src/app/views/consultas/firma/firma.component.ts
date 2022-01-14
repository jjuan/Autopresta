import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import SignaturePad from "signature_pad";

@Component({
  selector: 'app-firma',
  templateUrl: './firma.component.html',
  styleUrls: ['./firma.component.sass']
})
export class FirmaComponent implements OnInit, AfterViewInit {
  datos = {
    modulo: 'Consultas',
    componente: 'Contrataciones',
    icono: 'fas fa-folder-open',
    titulo: 'Contrato',
    controlador: 'Contrato'
  }

  @ViewChild('firmaDigital', {static: true}) signaturePadElement: any
  signaturePad: any;
  firma: any

  constructor() {
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.signaturePad = new SignaturePad(this.signaturePadElement.nativeElement);
  }

  cambiarColor() {
    const rojo = Math.round(Math.random() * 255)
    const verde = Math.round(Math.random() * 255)
    const azul = Math.round(Math.random() * 255)
    this.signaturePad.penColor = 'rgb(' + rojo + ',' + verde + ',' + azul + ')'
  }

  limpiarFirma() {
    this.signaturePad.clear();
  }

  deshacer() {
    const datos = this.signaturePad.toData();
    if (datos) {
      datos.pop()
      this.signaturePad.fromData(datos)
    }
    this.signaturePad.clear()
  }

  descargar(dataURL: any, nombre: any) {
    if (navigator.userAgent.indexOf('Safari') > -1 && navigator.userAgent.indexOf('Chrome') === -1) {
      window.open(dataURL)
    } else {
      const blob = this.URLtoBlob(dataURL)
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = nombre
      this.firma = blob
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
    }
  }

  URLtoBlob(dataURL: any) {
    const partes = dataURL.split(';base64,')
    const contentType = partes[0].split(':')[1]
    const raw = window.atob(partes[1])
    const rawL = raw.length
    const array = new Uint8Array(rawL)
    for (let i = 0; i < rawL; i++) {
      array[i] = raw.charCodeAt(i)
    }
    return new Blob([array], {type: contentType})
  }

  guardarPNG() {
    if (this.signaturePad.isEmpty()) {
      alert('Firmalo')
    } else {
      const u = this.signaturePad.toDataURL()
      this.descargar(u, 'firma.png')
      this.firma = u
    }
  }

  guardarJPG() {
    if (this.signaturePad.isEmpty()) {
      alert('Firmalo')
    } else {
      const u = this.signaturePad.toDataURL('image/jpeg')
      this.descargar(u, 'firma.jpg')
      this.firma = u
    }
  }

  guardarSVG() {
    if (this.signaturePad.isEmpty()) {
      alert('Firmalo')
    } else {
      const u = this.signaturePad.toDataURL('image/svg+xml')
      this.descargar(u, 'firma.svg')
      this.firma = u
    }
  }
}
