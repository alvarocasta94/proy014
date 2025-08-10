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

import es.cic.curso25.proy014.model.Vehiculo;
import es.cic.curso25.proy014.model.Multa;
import es.cic.curso25.proy014.service.VehiculoService;

@RestController
@RequestMapping("/vehiculo")
public class VehiculoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VehiculoController.class);

    @Autowired
    VehiculoService vehiculoService;

    // CRUD VEHÍCULO

    // CREATE
    @PostMapping
    public Vehiculo postVehiculo(@RequestBody Vehiculo vehiculo) {
        LOGGER.info("Método post a la ruta /vehiculo (crear vehiculo)");
        return vehiculoService.crearVehiculo(vehiculo);
    }

    // READ
    @GetMapping("/{id}")
    public Vehiculo getVehiculo(@PathVariable Long id) {
        LOGGER.info(String.format("Método get a la ruta /vehiculo/%d (obtener vehiculo)", id));
        return vehiculoService.getVehiculo(id);
    }

    @GetMapping
    public List<Vehiculo> getAllVehiculos() {
        LOGGER.info("Método get a la ruta /vehiculo (obtener todas los vehiculos)");
        return vehiculoService.getAllVehiculos();
    }

    // UPDATE
    @PutMapping("/{id}")
    public Vehiculo updateVehiculo(@PathVariable Long id, @RequestBody Vehiculo vehiculoActualizado) {
        LOGGER.info(String.format("Método PUT a la ruta /vehiculo/%d (actualizar vehiculo)", id));
        return vehiculoService.updatevehiculo(id, vehiculoActualizado);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteVehiculo(@PathVariable Long id) {
        LOGGER.info(String.format("Método DELETE a la ruta /vehiculo/%d (eliminar vehiculo)", id));
        vehiculoService.deleteVehiculo(id);
    }

    // CRUD MULTA

    // CREATE
    @PostMapping("/{idVehiculo}/multa")
    public Multa agregarMulta(@PathVariable Long idVehiculo, @RequestBody Multa multa) {
        LOGGER.info(String.format("Método POST a la ruta /vehiculo/%d (agregar multa al vehiculo)", idVehiculo));
        return vehiculoService.agregarMulta(idVehiculo, multa);
    }

    // READ
    @GetMapping("/{idVehiculo}/multa/{idMulta}")
    public Multa getMulta(@PathVariable Long idVehiculo, @PathVariable Long idMulta) {
        LOGGER.info(String.format("Método get a la ruta /vehiculo/%d/%d (obtener multa)", idVehiculo, idMulta));
        return vehiculoService.getMultaById(idVehiculo, idMulta);
    }

    @GetMapping("/{idVehiculo}/multa")
    public List<Multa> getAllMultas(@PathVariable Long idVehiculo) {
        LOGGER.info("Método get a la ruta /vehiculo (obtener todas las multas)");
        return vehiculoService.getMultasByVehiculo(idVehiculo);
    }

    // UPDATE
    @PutMapping("/{idVehiculo}/multa/{idMulta}")
    public Multa updateMultaEnVehiculo(@PathVariable Long idVehiculo, @PathVariable Long idMulta,
            @RequestBody Multa multa) {
        LOGGER.info(
                String.format("Método PUT a la ruta /vehiculo/%d/%d (actualizar multa en vehiculo)", idVehiculo,
                        idMulta, multa));
        return vehiculoService.updateMulta(idVehiculo, idMulta, multa);
    }

    // DELETE
    @DeleteMapping("/{idVehiculo}/multa/{idMulta}")
    public void deleteMulta(@PathVariable Long idVehiculo, @PathVariable Long idMulta) {
        LOGGER.info(String.format("Método DELETE a la ruta /vehiculo/%d/%d (eliminar multa en vehiculo)", idVehiculo,
                idMulta));
        vehiculoService.eliminarMulta(idVehiculo, idMulta);
    }

}
