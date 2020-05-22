package com.app.delhidarbar.view.adapter.dashboard_parent

import android.app.Activity
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.delhidarbar.R
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.model.dashboard.home.Category
import com.app.delhidarbar.model.dashboard.home.Parent_Home
import com.app.delhidarbar.model.dashboard.home.Product
import com.app.delhidarbar.utils.ClickVIewCartInterface
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.utils.IsVegOrNonVeg
import com.app.delhidarbar.view.fragments.HomeFragment
import com.app.delhidarbar.view.fragments.Select_restaurent

class DashboardParentAdapter(private val context: Activity, var pveg: String, var restaurants: Parent_Home, var veg: String, var firstTime: Boolean) : RecyclerView.Adapter<DashboardParentAdapter.ViewHolder>() {
    //type=0->veg 1->nonveg
    var clickWithTxt: ClickVIewCartInterface? = null
    var isVegOrNonVeg: IsVegOrNonVeg? = null
    lateinit var productsAdapter: ProductsAdapter

    companion object{
        var isLoadingg: Boolean = false
    }

    fun performItemClickIhaveToVisible(clickWithTxt: ClickVIewCartInterface) {
        this.clickWithTxt = clickWithTxt
    }

    fun isVegOrNonVeg(clickWithTxt: IsVegOrNonVeg) {
        this.isVegOrNonVeg = clickWithTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dashoard_parent, null)
        return ViewHolder(view)
    }

    fun getMoreItems(productsAdapter: ProductsAdapter, newlist: ArrayList<Product>) {
        /*  isLoadingg = false
            for (i in 0 until restaurants.items.size) {
                if (i >= 10) {
                    newlist.add(restaurants.items[i])
                }
            }
        */
        productsAdapter.addData(restaurants.items)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                val offer = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                holder.rv_childDashboard.layoutManager = offer

                val childOfferAdapter = ChildOfferAdapter(context, restaurants.offers)
                holder.rv_childDashboard.adapter = childOfferAdapter
                childOfferAdapter.performItemClick(object : ClickVIewCartInterface {
                    override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: Double, from: String) {
                        clickWithTxt!!.iWant2EnableMyViewCart(position, isEnable, actualPriceOfItems, "childOfferAdapter")
                    }
                })
            }
            1 -> {
                //for categories
                val offer = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                holder.rv_childDashboard.layoutManager = offer

                val value = restaurants.categories.any { x -> x.name == context.resources.getString(R.string.all) }
                if (value == false) {
                    restaurants.categories.add(0, Category(0, context.resources.getString(R.string.all)))
                }

                val childCategoriesAdapter = ChildCategoriesAdapter(context, restaurants.categories)
                holder.rv_childDashboard.adapter = childCategoriesAdapter
                childCategoriesAdapter.performItemClick(object : ClickVIewCartInterface {
                    override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: Double, from: String) {
                        clickWithTxt!!.iWant2EnableMyViewCart(position, isEnable, actualPriceOfItems, "childCategoriesAdapter")
                    }
                })

                if (HomeFragment.lastCheckedPos != null) {
                    holder.rv_childDashboard.getLayoutManager()?.scrollToPosition(HomeFragment.lastCheckedPos)
                }
            }
            2 -> {
                holder.rl_itemsHeading.visibility = View.VISIBLE
                if (!DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.SELECTED_VALUE).equals("")) {
                    holder.tv_headingStarter.setText(DelhiDarbar.instance!!.mySharedPrefrence.getString(ConstantKeys.SELECTED_VALUE))
                } else {
                    holder.tv_headingStarter.setText(context.resources.getString(R.string.all))
                }

                val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                holder.rv_childDashboard.layoutManager = linearLayoutManager

                //filter
                var newlist: ArrayList<Product> = ArrayList()
                if (veg.equals("5")) {

                    if(Select_restaurent.firstLogin){
                        Select_restaurent.firstLogin = false
                        newlist.clear()
                        for (i in 0 until restaurants.items.size) {
                            if (i < 10) {
                                newlist.add(restaurants.items[i])
                            }
                        }

                        //isLoadingg = true
                        productsAdapter = ProductsAdapter(context, veg, newlist, firstTime)
                        holder.rv_childDashboard.adapter = productsAdapter
                        holder.rv_childDashboard.getViewTreeObserver()
                                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        holder.rv_childDashboard.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                                        Handler().postDelayed({
                                            getMoreItems(productsAdapter, newlist)
                                        }, 1000)
                                    }
                                })

 /*
                        holder.rv_childDashboard.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {

                            override fun getTotalPageCount(): Int {
                                return totalPageCount
                            }

                            override fun isLastPage(): Boolean {
                                return isLastPagee
                            }

                            override fun isLoading(): Boolean {
                                return isLoadingg
                            }

                            override fun loadMoreItems() {
                                isLoadingg = true
                                getMoreItems(productsAdapter, newlist)
                            }
                        })
*/

                    }else{
                        productsAdapter = ProductsAdapter(context, veg, restaurants.items as ArrayList<Product>, firstTime)
                        holder.rv_childDashboard.adapter = productsAdapter
                    }

                    productsAdapter.performItemClick(object : ClickVIewCartInterface {
                        override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: Double, from: String) {
                            clickWithTxt!!.iWant2EnableMyViewCart(position, isEnable, actualPriceOfItems, "childItemsAdapter")
                        }
                    })

                } else {
                    if (veg.equals("0")) {
                        // veg
                        newlist.clear()
                        for (i in 0 until restaurants.items.size) {
                            if (restaurants.items[i].type.equals("0")) {
                                newlist.add(restaurants.items[i])
                            }
                        }

                    } else if (veg.equals("1")) {   // vegan
                        newlist.clear()
                        for (i in 0 until restaurants.items.size) {
                            if (restaurants.items[i].all_type.equals("vegan")) {
                                newlist.add(restaurants.items[i])
                            }
                        }

                    } else if (veg.equals("2")) {
                        // veg or vegan
                        newlist.clear()
                        for (i in 0 until restaurants.items.size) {
                            if (restaurants.items[i].type.equals("0") || restaurants.items[i].all_type.equals("vegan")) {
                                newlist.add(restaurants.items[i])
                            }
                        }

                    } else if (veg.equals("3")) {   // non-veg
                        newlist.clear()
                        for (i in 0 until restaurants.items.size) {
                            if (restaurants.items[i].type.equals("1")) {
                                newlist.add(restaurants.items[i])
                            }
                        }
                    }

                    if (newlist.size == 0) {
                        holder.rv_childDashboard.visibility = View.GONE
                        holder.tv_not_found.visibility = View.VISIBLE

                    } else {
                        holder.tv_not_found.visibility = View.GONE
                        holder.rv_childDashboard.visibility = View.VISIBLE

                        productsAdapter = ProductsAdapter(context, veg, newlist, firstTime)
                        holder.rv_childDashboard.adapter = productsAdapter

                        productsAdapter.performItemClick(object : ClickVIewCartInterface {
                            override fun iWant2EnableMyViewCart(position: Int, isEnable: Int, actualPriceOfItems: Double, from: String) {
                                clickWithTxt!!.iWant2EnableMyViewCart(position, isEnable, actualPriceOfItems, "childItemsAdapter")
                            }
                        })

                    }
                }

                if (veg == "0") {
                    holder.sb_veg.isChecked = true
                    holder.sb_vegan.isChecked = false

                } else if (veg == "1") {
                    holder.sb_veg.isChecked = false
                    holder.sb_vegan.isChecked = true

                } else if (veg == "2") {
                    holder.sb_veg.isChecked = true
                    holder.sb_vegan.isChecked = true

                } else if (veg == "3" || veg == "5") {
                    holder.sb_veg.isChecked = false
                    holder.sb_vegan.isChecked = false
                }

                holder.sb_vegan.setOnCheckedChangeListener { view, isChecked ->
                    var cat_id: String
                    if (holder.tv_headingStarter.text.equals(context.resources.getString(R.string.all))) {
                        cat_id = "0"
                    } else {
                        cat_id = restaurants.items[position].category_id.toString()
                    }

                    if(veg.equals(pveg)){
                        if(pveg.equals("0") && !isChecked && !holder.sb_veg.isChecked){
                            pveg = "3"
                        } else if(pveg.equals("0") && isChecked && holder.sb_veg.isChecked){
                            pveg = "2"
                        } else if(pveg.equals("0") && isChecked && holder.sb_veg.isChecked==false){
                            pveg = "1"
                        }else if(pveg.equals("1") && !isChecked && holder.sb_veg.isChecked== false){
                            pveg = "3"
                        } else if(pveg.equals("1") && isChecked && holder.sb_veg.isChecked){
                            pveg = "2"
                        } else if(pveg.equals("1") && !isChecked && holder.sb_veg.isChecked){
                            pveg = "0"
                        }else if(pveg.equals("2") && !isChecked && holder.sb_veg.isChecked== false){
                            pveg = "3"
                        } else if(pveg.equals("2") && !isChecked && holder.sb_veg.isChecked){
                            pveg = "0"
                        } else if(pveg.equals("2") && isChecked && holder.sb_veg.isChecked==false){
                            pveg = "1"
                        }else if(pveg.equals("5") && isChecked && holder.sb_veg.isChecked== false){
                            pveg = "1"
                        } else if(pveg.equals("5") && !isChecked && holder.sb_veg.isChecked){
                            pveg = "0"
                        } else if(pveg.equals("5") && isChecked && holder.sb_veg.isChecked){
                            pveg = "2"
                        }
                    }

                    if(!pveg.equals(veg)){
                        if (isChecked && holder.sb_veg.isChecked == false) {
                            isVegOrNonVeg!!.iWant2EnableVegOrNon("1", cat_id)

                        } else if (!isChecked && holder.sb_veg.isChecked) {
                            isVegOrNonVeg!!.iWant2EnableVegOrNon("0", cat_id)

                        } else if (isChecked && holder.sb_veg.isChecked == true) {
                            isVegOrNonVeg!!.iWant2EnableVegOrNon("2", cat_id)

                        } else if (!isChecked && holder.sb_veg.isChecked == false) {
                            isVegOrNonVeg!!.iWant2EnableVegOrNon("5", cat_id)
                        }
                    }

                }

                holder.sb_veg.setOnCheckedChangeListener { view, isChecked ->
                    var cat_id: String
                    if (holder.tv_headingStarter.text.equals(context.resources.getString(R.string.all))) {
                        cat_id = "0"
                    } else {
                        cat_id = restaurants.items[position].category_id.toString()
                    }

                    if(veg.equals(pveg)){
                        if(pveg.equals("0") && !isChecked && holder.sb_vegan.isChecked== false){
                            pveg = "3"
                        } else if(pveg.equals("0") && isChecked && holder.sb_vegan.isChecked){
                            pveg = "2"
                        } else if(pveg.equals("0") && !isChecked && holder.sb_vegan.isChecked){
                            pveg = "1"
                        }else if(pveg.equals("1") && !isChecked && holder.sb_vegan.isChecked== false){
                            pveg = "3"
                        } else if(pveg.equals("1") && isChecked && holder.sb_vegan.isChecked){
                            pveg = "2"
                        } else if(pveg.equals("1") && isChecked && holder.sb_vegan.isChecked==false){
                            pveg = "0"
                        }else if(pveg.equals("2") && !isChecked && holder.sb_vegan.isChecked== false){
                            pveg = "3"
                        } else if(pveg.equals("2") && !isChecked && holder.sb_vegan.isChecked){
                            pveg = "1"
                        } else if(pveg.equals("2") && isChecked && holder.sb_vegan.isChecked==false){
                            pveg = "0"
                        }else if(pveg.equals("5") && isChecked && holder.sb_vegan.isChecked== false){
                            pveg = "0"
                        } else if(pveg.equals("5") && !isChecked && holder.sb_vegan.isChecked==true){
                            pveg = "1"
                        } else if(pveg.equals("5") && isChecked && holder.sb_vegan.isChecked){
                            pveg = "2"
                        }
                    }

                    if(!pveg.equals(veg)){
                        if (isChecked && holder.sb_vegan.isChecked == false) {
                            isVegOrNonVeg!!.iWant2EnableVegOrNon("0", cat_id)

                        } else if (!isChecked && holder.sb_vegan.isChecked) {
                            isVegOrNonVeg!!.iWant2EnableVegOrNon("1", cat_id)

                        } else if (isChecked && holder.sb_vegan.isChecked == true) {
                            isVegOrNonVeg!!.iWant2EnableVegOrNon("2", cat_id)

                        }  else if (!isChecked && holder.sb_vegan.isChecked == false) {
                            isVegOrNonVeg!!.iWant2EnableVegOrNon("5", cat_id)

                        }

                    }

                }

            }
        }
    }

    override fun getItemCount(): Int {
        return if (restaurants == null) 0 else 3
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rv_childDashboard: RecyclerView = itemView.findViewById(R.id.rv_childDashboard)
        var sb_veg: SwitchCompat = itemView.findViewById(R.id.sb_veg)
        var sb_vegan: SwitchCompat = itemView.findViewById(R.id.sb_vegan)
        var rl_itemsHeading: RelativeLayout = itemView.findViewById(R.id.rl_itemsHeading)
        var tv_headingStarter: TextView = itemView.findViewById(R.id.tv_headingStarter)
        var tv_not_found: TextView = itemView.findViewById(R.id.tv_not_found)
    }

}
