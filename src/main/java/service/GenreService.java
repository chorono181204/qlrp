package service;

import dao.GenreDAO;
import model.Genre;

import java.util.List;
import java.util.Set;

public class GenreService {
    private static final Set<String> ALLOWED_SORTS = Set.of("id", "name");
    private final GenreDAO genreDAO;

    public GenreService() {
        this.genreDAO = new GenreDAO();
    }

    public List<Genre> findAll(String sortBy) {
        return genreDAO.findAll(normalizeSort(sortBy));
    }

    public List<Genre> search(String keyword, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(sortBy);
        }
        return genreDAO.search(keyword.trim(), normalizeSort(sortBy));
    }

    public String create(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Ten the loai khong duoc de trong.";
        }

        String normalizedName = name.trim();
        if (genreDAO.existsByName(normalizedName)) {
            return "The loai da ton tai.";
        }

        return genreDAO.create(normalizedName) ? "Them the loai thanh cong." : "Them the loai that bai.";
    }

    public String delete(int id) {
        if (id <= 0) {
            return "ID the loai khong hop le.";
        }

        return genreDAO.deleteById(id) ? "Xoa the loai thanh cong." : "Khong xoa duoc the loai.";
    }

    public String update(String genreIdText, String name) {
        int genreId;

        try {
            genreId = Integer.parseInt(genreIdText.trim());
        } catch (NumberFormatException e) {
            return "ID the loai phai la so nguyen.";
        }

        if (genreId <= 0) {
            return "ID the loai khong hop le.";
        }

        if (name == null || name.trim().isEmpty()) {
            return "Ten the loai khong duoc de trong.";
        }

        return genreDAO.update(genreId, name.trim()) ? "Cap nhat the loai thanh cong." : "Cap nhat the loai that bai.";
    }

    private String normalizeSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "id";
        }
        String normalized = sortBy.trim().toLowerCase();
        return ALLOWED_SORTS.contains(normalized) ? normalized : "id";
    }
}
