package org.example.backend_fivegivechill.repository;

import org.example.backend_fivegivechill.entity.SubscriptionUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionUserRepository extends JpaRepository<SubscriptionUserEntity, Integer> {

    @Query(value = "select top 1 * " +
            "from subscription_user " +
            "where user_id = ?1 " +
            "and last_day > GETDATE()", nativeQuery = true)
    SubscriptionUserEntity findSub(int id);

    @Query("SELECT CASE WHEN COUNT(su) > 0 THEN true ELSE false END FROM SubscriptionUserEntity su WHERE su.userEntity.id = :userId AND su.lastDay > CURRENT_DATE")
    Boolean isSubscribed(@Param("userId") int userId);


    @Query("SELECT su FROM SubscriptionUserEntity su WHERE su.userEntity.id = :userId AND su.lastDay > CURRENT_DATE ORDER BY su.id DESC")
    List<SubscriptionUserEntity> findActiveByUserId(@Param("userId") int userId);

    @Query("SELECT su FROM SubscriptionUserEntity su WHERE su.userEntity.id = :userId ORDER BY su.id DESC")
    List<SubscriptionUserEntity> findAllByUserId(@Param("userId") int userId);


    // thống kê
    @Query(value = "SELECT SUM(price) AS total_revenue " +
            "FROM subscription_user " +
            "WHERE YEAR(create_date) = YEAR(GETDATE()) " +
            "AND MONTH(create_date) = MONTH(GETDATE())", nativeQuery = true)
    Double statisticsByMonth();

    @Query(value = "SELECT SUM(price) AS total_revenue " +
            "FROM subscription_user " +
            "WHERE YEAR(create_date) = YEAR(GETDATE())", nativeQuery = true)
    Double statisticsByYear();

    @Query(value = "SELECT COUNT(*) " +
            "FROM Users " +
            "WHERE role = 1", nativeQuery = true)
    int statisticsSumUsers();

    // creator
    @Query(value = "SELECT COUNT(*) " +
            "FROM follow " +
            "WHERE user_id = ?1", nativeQuery = true)
    int statisticsSumUsersCreator(int id);

    @Query(value = "SELECT COUNT(*) " +
            "FROM Users " +
            "WHERE role = 2", nativeQuery = true)
    int statisticsSumCreator();

    @Query(value = "SELECT COUNT(*) " +
            "FROM Songs", nativeQuery = true)
    int statisticsSumSongs();

    // creator
    @Query(value = "SELECT COUNT(*) " +
            "FROM Songs " +
            "WHERE user_id = ?1 ", nativeQuery = true)
    int statisticsSumSongsCreator(int id);

    // Đếm số lượng album của creator
    @Query(value = "SELECT COUNT(*) " +
            "FROM Album " +
            "WHERE user_id = ?1 ", nativeQuery = true)
    int statisticsSumAlbumCreator(int id);

    @Query(value = "SELECT TOP 5 id, name, count_listens " +
            "FROM Songs " +
            "ORDER BY count_listens DESC", nativeQuery = true)
    List<Object[]> statisticsTop5Songs();


    // creator
    @Query(value = "SELECT TOP 5 id, name, count_listens " +
            "FROM Songs " +
            "WHERE user_id = ?1 " +
            "ORDER BY count_listens DESC", nativeQuery = true)
    List<Object[]> statisticsTop5SongsCretor(int userId);

    @Query(value = "SELECT MONTH(create_date) AS thang, YEAR(create_date) AS nam, SUM(price) AS doanhThu " +
            "FROM subscription_user " +
            "WHERE YEAR(create_date) = YEAR(GETDATE()) " +
            "GROUP BY MONTH(create_date), YEAR(create_date)", nativeQuery = true)
    List<Object[]> lineChart();

    @Query(value = "SELECT MONTH(create_date) AS thang, YEAR(create_date) AS nam, SUM(price) AS doanhThu " +
            "FROM subscription_user " +
            "WHERE create_date BETWEEN ?1 AND ?2 " +
            "GROUP BY MONTH(create_date), YEAR(create_date)", nativeQuery = true)
    List<Object[]> lineChartByTime(String first, String last);

    @Query(value = "SELECT s.id, first_day, last_day, s.price, s.status, u.full_name, sp.name " +
            "FROM subscription_user s, users u, subscription_package sp " +
            "WHERE s.sub_id = sp.id " +
            "AND s.user_id = u.id", nativeQuery = true)
    Page<Object[]> listSubPackUser(Pageable pageable);
    // thống kê
}
