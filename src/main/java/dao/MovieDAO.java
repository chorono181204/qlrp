package dao;

import model.MovieSummary;
import utils.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public List<MovieSummary> findAll(String whereClause, String orderBy, String keyword) {
        String sql = """
                SELECT m.id,
                       m.title,
                       m.duration_minutes,
                       COALESCE(m.director, '') AS director,
                       m.status,
                       COALESCE(GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', '), '') AS genres
                FROM movies m
                LEFT JOIN movie_genres mg ON mg.movie_id = m.id
                LEFT JOIN genres g ON g.id = mg.genre_id
                """ + whereClause + """
                GROUP BY m.id, m.title, m.duration_minutes, m.director, m.status
                ORDER BY """ + orderBy;

        List<MovieSummary> movies = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (keyword != null) {
                String pattern = "%" + keyword + "%";
                preparedStatement.setString(1, pattern);
                preparedStatement.setString(2, pattern);
                preparedStatement.setString(3, pattern);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    movies.add(new MovieSummary(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getInt("duration_minutes"),
                            resultSet.getString("director"),
                            resultSet.getString("status"),
                            resultSet.getString("genres")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach phim: " + e.getMessage());
        }

        return movies;
    }

    public boolean create(String title, int durationMinutes, String director, String language,
                          String ageRating, String description, String status, List<Integer> genreIds) {
        String movieSql = """
                INSERT INTO movies (title, duration_minutes, director, language, age_rating, description, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        String movieGenreSql = "INSERT INTO movie_genres (movie_id, genre_id) VALUES (?, ?)";

        try (Connection connection = JdbcUtil.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement movieStatement =
                         connection.prepareStatement(movieSql, Statement.RETURN_GENERATED_KEYS)) {
                movieStatement.setString(1, title);
                movieStatement.setInt(2, durationMinutes);
                movieStatement.setString(3, emptyToNull(director));
                movieStatement.setString(4, emptyToNull(language));
                movieStatement.setString(5, emptyToNull(ageRating));
                movieStatement.setString(6, emptyToNull(description));
                movieStatement.setString(7, status);
                movieStatement.executeUpdate();

                int movieId;
                try (ResultSet generatedKeys = movieStatement.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        connection.rollback();
                        return false;
                    }
                    movieId = generatedKeys.getInt(1);
                }

                if (!genreIds.isEmpty()) {
                    try (PreparedStatement movieGenreStatement = connection.prepareStatement(movieGenreSql)) {
                        for (Integer genreId : genreIds) {
                            movieGenreStatement.setInt(1, movieId);
                            movieGenreStatement.setInt(2, genreId);
                            movieGenreStatement.addBatch();
                        }
                        movieGenreStatement.executeBatch();
                    }
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Loi khi them phim: " + e.getMessage());
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Loi khi ket noi de them phim: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi xoa phim: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int movieId, String title, int durationMinutes, String director, String language,
                          String ageRating, String description, String status, List<Integer> genreIds) {
        String movieSql = """
                UPDATE movies
                SET title = ?, duration_minutes = ?, director = ?, language = ?, age_rating = ?, description = ?, status = ?
                WHERE id = ?
                """;
        String deleteGenresSql = "DELETE FROM movie_genres WHERE movie_id = ?";
        String insertGenreSql = "INSERT INTO movie_genres (movie_id, genre_id) VALUES (?, ?)";

        try (Connection connection = JdbcUtil.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement movieStatement = connection.prepareStatement(movieSql);
                 PreparedStatement deleteGenresStatement = connection.prepareStatement(deleteGenresSql)) {
                movieStatement.setString(1, title);
                movieStatement.setInt(2, durationMinutes);
                movieStatement.setString(3, emptyToNull(director));
                movieStatement.setString(4, emptyToNull(language));
                movieStatement.setString(5, emptyToNull(ageRating));
                movieStatement.setString(6, emptyToNull(description));
                movieStatement.setString(7, status);
                movieStatement.setInt(8, movieId);

                if (movieStatement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }

                deleteGenresStatement.setInt(1, movieId);
                deleteGenresStatement.executeUpdate();

                if (!genreIds.isEmpty()) {
                    try (PreparedStatement insertGenreStatement = connection.prepareStatement(insertGenreSql)) {
                        for (Integer genreId : genreIds) {
                            insertGenreStatement.setInt(1, movieId);
                            insertGenreStatement.setInt(2, genreId);
                            insertGenreStatement.addBatch();
                        }
                        insertGenreStatement.executeBatch();
                    }
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Loi khi cap nhat phim: " + e.getMessage());
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Loi khi ket noi de cap nhat phim: " + e.getMessage());
            return false;
        }
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
