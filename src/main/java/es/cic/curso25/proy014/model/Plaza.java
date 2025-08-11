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
    private boolean ocupado;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;

    public void setVehiculo(Vehiculo nuevoVehiculo) {
        // Si ya hay un vehículo asignado, lo desvinculamos
        if (this.vehiculo != null && this.vehiculo.getPlaza() == this) {
            this.vehiculo.setPlaza(null);
        }

        this.vehiculo = nuevoVehiculo;

        // Si el nuevo vehículo no está null y no está vinculado todavía, lo vinculamos
        if (nuevoVehiculo != null && nuevoVehiculo.getPlaza() != this) {
            nuevoVehiculo.setPlaza(this);
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

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
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
        return "Plaza [id=" + id + ", numero=" + numero + ", disponible=" + disponible + ", ocupado=" + ocupado + "]";
    }

    

   

}
