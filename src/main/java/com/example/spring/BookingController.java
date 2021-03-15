package com.example.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;

@RestController
public class BookingController {
    @Autowired
    private BookingRepository repository;

    @GetMapping("/bookings")
    List<Booking> all() {
        return repository.findAll();
    }

    @GetMapping("/bookings/{id}")
    Booking all(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
    }

    @PostMapping("/bookings")
    Booking post(@RequestBody Booking booking) {
        return repository.save(booking);
    }

    @PutMapping("/bookings/{id}")
    Booking put(@RequestBody Booking booking, @PathVariable Long id) {
        return repository.findById(id).map(
                b -> {
                    b.setBookingName(booking.getBookingName());
                    return repository.save(b);
                }
        ).orElseGet( () -> {
            return repository.save(booking);
        });
    }

    @DeleteMapping("/bookings/{id}")
    void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

@ControllerAdvice
class BookingNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String bookingNotFound(BookingNotFoundException e) {
        return e.getMessage();
    }
}

class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Unable to find booking "+id);
    }
}

@Configuration
class LoadBookingDb {
    private static final Logger log = LoggerFactory.getLogger(LoadBookingDb.class);

    @Bean
    CommandLineRunner initDb(BookingRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Booking("Booking 1")));
            log.info("Preloading " + repository.save(new Booking("Booking 2")));
        };
    }
}

interface BookingRepository extends JpaRepository<Booking, Long> { }

@Entity
class Booking {
    @Id @GeneratedValue
    private Long id;
    private String bookingName;

    public Booking() {
    }

    public Booking(String bookingName) {
        this.bookingName = bookingName;
    }

    public Long getId() {
        return id;
    }

    public String getBookingName() {
        return bookingName;
    }

    public void setBookingName(String bookingName) {
        this.bookingName = bookingName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(bookingName, booking.bookingName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookingName);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", bookingName='" + bookingName + '\'' +
                '}';
    }
}
