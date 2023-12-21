package ng.org.asicts.election.util

import ng.org.asicts.election.R
import ng.org.asicts.election.model.Aspirants
import ng.org.asicts.election.util.Constants.DIRECTOR_OF_INFORMATION

object AspirantsData {

    val presidentAspirantList = listOf(
        Aspirants(
            "Eme Chikezie Greatman",
            "Software Engineering",
            "300 Level",
            R.drawable.pres_kez,
            Constants.PRESIDENT
        )
    )

    val vicePresidentAspirantList = listOf(
        Aspirants(
            "Ogbenna Anita Adaugo",
            "Computer Science",
            "300 Level",
            R.drawable.vp_anita,
            Constants.VICE_PRESIDENT
        ),
        Aspirants(
            "Ikefuna Stella Lebechi",
            "Information Technology",
            "300 Level",
            R.drawable.vp_stella,
            Constants.VICE_PRESIDENT
        )
    )

    val secretaryGeneralAspirantList = listOf(
        Aspirants(
            "Eze Anthony Ifeanyichukwu",
            "Information Technology",
            "300 Level",
            R.drawable.sec_eze,
            Constants.SECRETARY_GENERAL
        )
    )

    val financialSecretaryAspirantList = listOf(
        Aspirants(
            "Okop Owaji-Iroyem Okikere",
            "Computer Science",
            "300 Level",
            R.drawable.fin_roy,
            Constants.FINANCIAL_SECRETARY
        ),
        Aspirants(
            "Okutu Richard Chiedu",
            "Cybersecurity",
            "300 Level",
            R.drawable.fin_richard,
            Constants.FINANCIAL_SECRETARY
        )
    )

    val directorOfInformationAspirantList = listOf(
        Aspirants(
            "Ezeala Donnel Chimemerie",
            "Computer Science",
            "200 Level",
            R.drawable.doi_donnel,
            DIRECTOR_OF_INFORMATION
        )
    )

    val directorOfIctAspirantList = listOf(
        Aspirants(
            "Obidigbo Chiedozie Nzubechukwu",
            "Cybersecurity",
            "200 Level",
            R.drawable.ict_chiedozie,
            Constants.DIRECTOR_OF_ICT
        )
    )

    val directorOfWelfareAspirantList = listOf(
        Aspirants(
            "Ezema Gospel Oluwatobi",
            "Cybersecurity",
            "200 Level",
            R.drawable.welfare_gospel,
            Constants.DIRECTOR_OF_WELFARE
        )
    )

    val directorOfSocialAspirantList = listOf(
        Aspirants(
            "Eni Samson",
            "Computer Science",
            "300 Level",
            R.drawable.dosocials_samson,
            Constants.DIRECTOR_OF_SOCIAL
        )
    )

    val directorOfSportsAspirantList = listOf(
        Aspirants(
            "Chileziem Blessed",
            "Software Engineering",
            "300 Level",
            R.drawable.dosports_blessed,
            Constants.DIRECTOR_OF_SPORTS
        )
    )
}