package service;

import dao.CinemaDAO;
import model.Cinema;

import java.util.List;
import java.util.Set;

public class CinemaService {
    private static final Set<String> ALLOWED_SORTS = Set.of("id", "name", "address");
    private final CinemaDAO cinemaDAO;

    public CinemaService() {
        this.cinemaDAO = new CinemaDAO();
    }

    public List<Cinema> findAll(String sortBy) {
        return cinemaDAO.findAll("", normalizeSort(sortBy), null);
    }

    public List<Cinema> search(String keyword, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(sortBy);
        }
        return cinemaDAO.findAll(
                "WHERE LOWER(name) LIKE LOWER(?) OR LOWER(address) LIKE LOWER(?)",
                normalizeSort(sortBy),
                keyword.trim()
        );
    }

    public String create(String name, String address, String phone) {
        if (name == null || name.trim().isEmpty()) {
            return "Ten rap khong duoc de trong.";
        }

        if (address == null || address.trim().isEmpty()) {
            return "Dia chi rap khong duoc de trong.";
        }

        return cinemaDAO.create(name.trim(), address.trim(), phone) ? "Them rap thanh cong." : "Them rap that bai.";
    }

    public String update(String cinemaIdText, String name, String address, String phone) {
        int cinemaId;

        try {
            cinemaId = Integer.parseInt(cinemaIdText.trim());
        } catch (NumberFormatException e) {
            return "ID rap phai la so nguyen.";
        }

        if (cinemaId <= 0) {
            return "ID rap khong hop le.";
        }

        if (name == null || name.trim().isEmpty()) {
            return "Ten rap khong duoc de trong.";
        }

        if (address == null || address.trim().isEmpty()) {
            return "Dia chi rap khong duoc de trong.";
        }

        return cinemaDAO.update(cinemaId, name.trim(), address.trim(), phone)
                ? "Cap nhat rap thanh cong."
                : "Cap nhat rap that bai.";
    }

    public String delete(String cinemaIdText) {
        int cinemaId;

        try {
            cinemaId = Integer.parseInt(cinemaIdText.trim());
        } catch (NumberFormatException e) {
            return "ID rap phai la so nguyen.";
        }

        if (cinemaId <= 0) {
            return "ID rap khong hop le.";
        }

        return cinemaDAO.deleteById(cinemaId) ? "Xoa rap thanh cong." : "Xoa rap that bai.";
    }

    private String normalizeSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "id";
        }
        String normalized = sortBy.trim().toLowerCase();
        return ALLOWED_SORTS.contains(normalized) ? normalized : "id";
    }
}
