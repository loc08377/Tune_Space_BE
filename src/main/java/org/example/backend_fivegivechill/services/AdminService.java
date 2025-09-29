package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.repository.SubscriptionUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    SubscriptionUserRepository subscriptionUserRepository;

    public Double statisticsByMonth() {
        Double result = subscriptionUserRepository.statisticsByMonth();
        return result != null ? result : 0.0;
    }

    public Double statisticsByYear(){
        Double result = subscriptionUserRepository.statisticsByYear();
        return result != null ? result : 0.0;
    }

    public int statisticsSumUsers(){
        return subscriptionUserRepository.statisticsSumUsers();
    }

    // creator
    public int statisticsSumUsersCreator(int id){
        return subscriptionUserRepository.statisticsSumUsersCreator(id);
    }

    public int statisticsSumCreator(){
        return subscriptionUserRepository.statisticsSumCreator();
    }

    public int statisticsSumSongs(){
        return subscriptionUserRepository.statisticsSumSongs();
    }

    // creator
    public int statisticsSumSongsCreator(int id){
        return subscriptionUserRepository.statisticsSumSongsCreator(id);
    }

    public List<Object[]> statisticsTop5Songs(){
        return subscriptionUserRepository.statisticsTop5Songs();
    }

    // creator
    public List<Object[]> statisticsTop5SongsCreator(int id){
        return subscriptionUserRepository.statisticsTop5SongsCretor(id);
    }

    public List<Object[]> lineChart(String first, String last){
        if(first.isEmpty() && last.isEmpty()){
            return subscriptionUserRepository.lineChart();
        }else if(!first.isEmpty() && !last.isEmpty()){
            return subscriptionUserRepository.lineChartByTime(first, last);
        }else if(first.isEmpty() && !last.isEmpty()){
            String nam = last.substring(0,4);
            first = nam+"-01-01";
            return subscriptionUserRepository.lineChartByTime(first, last);
        }else {
            String nam = first.substring(0,4);
            last = nam+"-12-31";
            return subscriptionUserRepository.lineChartByTime(first, last);
        }
    }

    public Page<Object[]> listSubPackUser(Pageable pageable){
        return subscriptionUserRepository.listSubPackUser(pageable);
    }
}
