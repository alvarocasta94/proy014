package es.cic.curso25.proy015.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import es.cic.curso25.proy015.exceptions.NotFoundException;
import es.cic.curso25.proy015.model.Plaza;
import es.cic.curso25.proy015.model.Vehiculo;
import es.cic.curso25.proy015.repository.PlazaRepository;
import es.cic.curso25.proy015.repository.VehiculoRepository;

public class PlazaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlazaService.class);

    @Autowired
    VehiculoRepository vehiculoRepository;

    @Autowired
    PlazaRepository plazaRepository;

    // CRUD PLAZA

    // READ
    @Transactional(readOnly = true)
    public Plaza getPlaza(Long id) {
        LOGGER.info(String.format("Buscando la plaza con id %d", id));
        Optional<Plaza> plaza = plazaRepository.findById(id);
        if (plaza.isEmpty()) {
            throw new NotFoundException(String.format("No se ha encontrado ninguna plaza con id %d", id));
        }
        return plaza.get();
    }

    @Transactional(readOnly = true)
    public List<Plaza> getAllPlazas() {
        LOGGER.info("Obteniendo todas las plazas");
        return plazaRepository.findAll();
    }

    // UPDATE
    @Transactional
    public Plaza updatePlaza(Long id, Plaza plaza) {
        LOGGER.info(String.format("Actualizando la plaza con id %d", id));
        // Comprobamos que exista un Plaza con ese id
        Plaza plazaEnBD = this.getPlaza(id);

        // Regla: no se puede poner como no disponible si está ocupada
        if (!plaza.isDisponible() && plazaEnBD.getVehiculoOcupante() != null) {
            throw new IllegalArgumentException("No se puede marcar la plaza como no disponible si está ocupada");
        }

        plazaEnBD.setDisponible(plaza.isDisponible());

        return plazaRepository.save(plazaEnBD);
    }

    // Asignar Vehículo a Plaza
    @Transactional
    public Plaza asignarVehiculoAPlaza(Long idPlaza, Vehiculo vehiculo) {
        LOGGER.info(String.format("Asignando vehículo con id %d a la plaza con id %d", vehiculo.getId(), idPlaza));

        Plaza plaza = getPlaza(idPlaza);

        if (plaza.getVehiculosAsignados().size() >= 5) {
            throw new IllegalStateException("No se puede asignar más de 5 vehículos a la misma plaza");
        }

        // Evitar duplicados
        if (!plaza.getVehiculosAsignados().contains(vehiculo)) {
            plaza.asignarVehiculo(vehiculo);
            // También actualizamos el vehículo para que apunte a la plaza asignada
            vehiculo.setPlazaAsignada(plaza);
            // Guardamos vehículo y plaza
            vehiculoRepository.save(vehiculo);
            plazaRepository.save(plaza);
        }

        return plaza;
    }

    // Desasignar Vehículo a Plaza
    @Transactional
    public Plaza desasignarVehiculoDePlaza(Long idPlaza, Vehiculo vehiculo) {
        LOGGER.info(String.format("Desasignando vehículo con id %d de la plaza con id %d", vehiculo.getId(), idPlaza));

        Plaza plaza = getPlaza(idPlaza);

        if (plaza.getVehiculosAsignados().contains(vehiculo)) {
            plaza.quitarVehiculo(vehiculo);
            // También limpiamos la plazaAsignada del vehículo
            if (vehiculo.getPlazaAsignada() != null && vehiculo.getPlazaAsignada().equals(plaza)) {
                vehiculo.setPlazaAsignada(null);
            }
            vehiculoRepository.save(vehiculo);
            plazaRepository.save(plaza);
        } else {
            throw new IllegalArgumentException("El vehículo no está asignado a la plaza indicada");
        }

        return plaza;
    }

    

}
