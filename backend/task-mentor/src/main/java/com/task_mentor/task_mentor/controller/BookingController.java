package com.task_mentor.task_mentor.controller;

import com.task_mentor.task_mentor.dto.BookingRequest;
import com.task_mentor.task_mentor.dto.BookingResponse;
import com.task_mentor.task_mentor.entity.Booking;
import com.task_mentor.task_mentor.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;


    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            Booking booking = bookingService.createBooking(
                    request.getStudentId(),
                    request.getMentorId(),
                    request.getTaskId(),
                    request.getProposedDatetime()
            );

            BookingResponse response = BookingResponse.fromEntity(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));
        }
    }


    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptBooking(
            @PathVariable Long id,
            @RequestParam Long mentorId) {
        try {
            Booking booking = bookingService.acceptBooking(id, mentorId);
            BookingResponse response = BookingResponse.fromEntity(booking);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse(e.getMessage()));
        }
    }


    @PutMapping("/{id}/decline")
    public ResponseEntity<?> declineBooking(
            @PathVariable Long id,
            @RequestParam Long mentorId) {
        try {
            Booking booking = bookingService.declineBooking(id, mentorId);
            BookingResponse response = BookingResponse.fromEntity(booking);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        }
    }


    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam String userType) {
        try {
            Booking booking = bookingService.cancelBooking(id, userId, userType);
            BookingResponse response = BookingResponse.fromEntity(booking);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            Booking booking = bookingService.getBookingById(id);
            BookingResponse response = BookingResponse.fromEntity(booking);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllBookings(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long mentorId,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) String status) {

        try {
            List<Booking> bookings;

            if (studentId != null) {
                bookings = bookingService.getBookingsByStudent(studentId);
            } else if (mentorId != null && status != null) {
                bookings = bookingService.getBookingsByMentorAndStatus(mentorId, status);
            } else if (mentorId != null) {
                bookings = bookingService.getBookingsByMentor(mentorId);
            } else if (taskId != null) {
                bookings = bookingService.getBookingsByTask(taskId);
            } else if (status != null) {
                bookings = bookingService.getBookingsByStatus(status);
            } else {
                bookings = bookingService.getAllBookings();
            }

            List<BookingResponse> responses = bookings.stream()
                    .map(BookingResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getBookingsByStudent(@PathVariable Long studentId) {
        try {
            List<Booking> bookings = bookingService.getBookingsByStudent(studentId);
            List<BookingResponse> responses = bookings.stream()
                    .map(BookingResponse::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<?> getBookingsByMentor(
            @PathVariable Long mentorId,
            @RequestParam(required = false) String status) {
        try {
            List<Booking> bookings;

            if (status != null) {
                bookings = bookingService.getBookingsByMentorAndStatus(mentorId, status);
            } else {
                bookings = bookingService.getBookingsByMentor(mentorId);
            }

            List<BookingResponse> responses = bookings.stream()
                    .map(BookingResponse::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }


    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> getBookingsByTask(@PathVariable Long taskId) {
        try {
            List<Booking> bookings = bookingService.getBookingsByTask(taskId);
            List<BookingResponse> responses = bookings.stream()
                    .map(BookingResponse::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        }
    }


    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}