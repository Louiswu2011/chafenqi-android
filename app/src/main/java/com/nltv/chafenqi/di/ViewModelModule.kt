package com.nltv.chafenqi.di

import com.nltv.chafenqi.CFQUserStateViewModel
import com.nltv.chafenqi.view.home.HomePageViewModel
import com.nltv.chafenqi.view.home.announcement.HomeAnnouncementPageViewModel
import com.nltv.chafenqi.view.home.leaderboard.HomeLeaderboardPageViewModel
import com.nltv.chafenqi.view.home.log.HomeLogPageViewModel
import com.nltv.chafenqi.view.home.log.LogDetailPageViewModel
import com.nltv.chafenqi.view.home.rating.HomeRatingPageViewModel
import com.nltv.chafenqi.view.home.recent.HomeRecentViewModel
import com.nltv.chafenqi.view.home.recent.RecentDetailPageViewModel
import com.nltv.chafenqi.view.home.team.HomeTeamIntroductionViewModel
import com.nltv.chafenqi.view.home.team.HomeTeamLeaderboardViewModel
import com.nltv.chafenqi.view.home.team.HomeTeamPageViewModel
import com.nltv.chafenqi.view.home.team.settings.HomeMemberViewModel
import com.nltv.chafenqi.view.home.team.settings.HomePendingMemberViewModel
import com.nltv.chafenqi.view.home.team.settings.HomeTeamCourseSettingViewModel
import com.nltv.chafenqi.view.login.LoginPageViewModel
import com.nltv.chafenqi.view.module.AppUpdaterViewModel
import com.nltv.chafenqi.view.premium.PremiumRedeemPageViewModel
import com.nltv.chafenqi.view.settings.SettingsPageViewModel
import com.nltv.chafenqi.view.songlist.SongDetailViewModel
import com.nltv.chafenqi.view.songlist.SongListPageViewModel
import com.nltv.chafenqi.view.songlist.comment.CommentPageViewModel
import com.nltv.chafenqi.view.songlist.record.MusicRecordPageViewModel
import com.nltv.chafenqi.view.songlist.stats.SongStatsPageViewModel
import com.nltv.chafenqi.view.updater.UpdaterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::CFQUserStateViewModel)
    viewModelOf(::LoginPageViewModel)
    viewModelOf(::HomePageViewModel)
    viewModelOf(::HomeAnnouncementPageViewModel)
    viewModelOf(::HomeLeaderboardPageViewModel)
    viewModelOf(::HomeLogPageViewModel)
    viewModelOf(::LogDetailPageViewModel)
    viewModelOf(::HomeRatingPageViewModel)
    viewModelOf(::HomeRecentViewModel)
    viewModelOf(::RecentDetailPageViewModel)
    viewModelOf(::HomeTeamIntroductionViewModel)
    viewModelOf(::HomeTeamLeaderboardViewModel)
    viewModelOf(::HomeTeamPageViewModel)
    viewModelOf(::HomeMemberViewModel)
    viewModelOf(::HomePendingMemberViewModel)
    viewModelOf(::HomeTeamCourseSettingViewModel)
    viewModelOf(::SongListPageViewModel)
    viewModelOf(::SongDetailViewModel)
    viewModelOf(::CommentPageViewModel)
    viewModelOf(::MusicRecordPageViewModel)
    viewModelOf(::SongStatsPageViewModel)
    viewModelOf(::SettingsPageViewModel)
    viewModelOf(::AppUpdaterViewModel)
    viewModelOf(::PremiumRedeemPageViewModel)
    viewModelOf(::UpdaterViewModel)
}
