import { Component, OnInit, Renderer2 } from '@angular/core';
import { AcreditacionMembresiaDataService } from '../../services/acreditacion-membresia-data.service';
import { BaseComponent } from '../../../../shared/base/base.component';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { CatalogosService } from '../../../../shared/catalogos/services/catalogos.service';
import { ModalService } from '../../../../shared/services/modal.service';
import { AcreditacionMembresiaService } from '../../services/acreditacion-membresia.service';
import { AlertService } from '../../../../shared/services/alert.service';
import { SharedService } from '../../../../shared/services/shared.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-acreditacionymembresia-acuse',
  standalone: true,
  imports: [CommonModule ],
  templateUrl: './acreditacionymembresia-acuse.component.html',
  styleUrl: './acreditacionymembresia-acuse.component.css'
})
export class AcreditacionymembresiaAcuseComponent extends BaseComponent  implements OnInit  {
 datosFormularioPrevio: any = {};

public Object = Object;

    constructor (
    private fb: FormBuilder,
    private router : Router,
    private renderer: Renderer2,
    private catalogosService: CatalogosService,
    private modalService: ModalService,
    private acreditacionMembresiaService: AcreditacionMembresiaService,
    private alertService: AlertService,
    private acreditacionMembresiaDataService: AcreditacionMembresiaDataService,
    sharedService: SharedService
  ) {
    super(sharedService);


  }


override ngOnInit(): void {
    // Obtener los datos del servicio
    this.datosFormularioPrevio = this.acreditacionMembresiaDataService.datosFormularioPrevio;

    console.log('Datos del formulario previo en Acuse:', this.datosFormularioPrevio);
  }



}
