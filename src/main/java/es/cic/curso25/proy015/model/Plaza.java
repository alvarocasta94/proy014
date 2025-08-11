package es.cic.curso25.proy015.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;

@Entity
public class Plaza {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String numero;
    private boolean disponible;

    @OneToMany(mappedBy = "plazaAsignada", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Vehiculo> vehiculosAsignados = new ArrayList<>();

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "vehiculo_ocupante_id")
    private Vehiculo vehiculoOcupante;

    public void asignarVehiculo(Vehiculo nuevoVehiculo) {
        if (nuevoVehiculo != null && !this.vehiculosAsignados.contains(nuevoVehiculo)) {
            this.vehiculosAsignados.add(nuevoVehiculo);

            // Mantener la relación bidireccional
            if (nuevoVehiculo.getPlazaAsignada() != this) {
                nuevoVehiculo.setPlazaAsignada(this);
            }
        }
    }

    public void quitarVehiculo(Vehiculo vehiculo) {
        if (this.vehiculosAsignados.remove(vehiculo)) {
            if (vehiculo.getPlazaAsignada() == this) {
                vehiculo.setPlazaAsignada(null);
            }
        }
    }

    public boolean ocuparConVehiculo(Vehiculo nuevoVehiculo) {
        // Si ya hay un vehículo ocupando la plaza, no permitimos otro
        if (this.vehiculoOcupante != null) {
            return false; // No se pudo ocupar porque ya está ocupada
        }

        // Asignamos el nuevo vehículo
        this.vehiculoOcupante = nuevoVehiculo;

        // Aseguramos la relación bidireccional
        if (nuevoVehiculo != null && nuevoVehiculo.getPlazaOcupada() != this) {
            nuevoVehiculo.setPlazaOcupada(this);
        }
        return true; // Operación exitosa
    }

    public void desocuparVehiculo() {
        if (this.vehiculoOcupante != null) {
            // Rompemos la relación bidireccional
            if (this.vehiculoOcupante.getPlazaOcupada() == this) {
                this.vehiculoOcupante.setPlazaOcupada(null);
            }
            this.vehiculoOcupante = null;
        }
    }

    public Plaza() {
    }

    public Plaza(String numero) {
        this.numero = numero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNumero() {
        return numero;
    }

    public List<Vehiculo> getVehiculosAsignados() {
        return vehiculosAsignados;
    }

    public Vehiculo getVehiculoOcupante() {
        return vehiculoOcupante;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Plaza other = (Plaza) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Plaza [id=" + id + ", numero=" + numero + ", disponible=" + disponible + "]";
    }

}
