package es.cic.curso25.proy014.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.cic.curso25.proy014.model.Plaza;
import es.cic.curso25.proy014.service.PlazaService;
import es.cic.curso25.proy014.service.VehiculoService;

@RestController
@RequestMapping("/plaza")
public class PlazaController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlazaController.class);

    @Autowired
    PlazaService plazaService;

    // CRUD PLAZA

    // READ
    @GetMapping("/{id}")
    public Plaza getPlaza(@PathVariable Long id) {
        LOGGER.info(String.format("Método get a la ruta /plaza/%d (obtener plaza)", id));
        return plazaService.getPlaza(id);
    }

    @GetMapping
    public List<Plaza> getAllPlazas() {
        LOGGER.info("Método get a la ruta /plaza (obtener todas las plazas)");
        return plazaService.getAllPlazas();
    }

    // UPDATE
    @PutMapping("/{id}")
    public Plaza updatePlaza(@PathVariable Long id, @RequestBody Plaza plazaActualizada) {
        LOGGER.info(String.format("Método PUT a la ruta /plaza/%d (actualizar plaza)", id));
        return plazaService.updatePlaza(id, plazaActualizada);
    }


    //Asignar Vehículo a Plaza
    @PutMapping("/{id}/asignarVehiculo/{vehiculoId}")
    public Plaza asignarVehiculoAPlaza(@PathVariable Long id, @PathVariable Long vehiculoId) {
        LOGGER.info(String.format("Asignar vehículo %d a plaza %d", vehiculoId, id));
        return plazaService.asignarVehiculo(id, vehiculoId);
    }

}
