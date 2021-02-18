package space.alena.kominch.adapters

import space.alena.kominch.models.AppDetail

interface ChangeHomeListener {
    fun refreshDataBase(appDetail: AppDetail, i: Int)
    fun addWebsite()
}