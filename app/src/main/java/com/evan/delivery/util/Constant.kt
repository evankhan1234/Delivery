package com.evan.delivery.util

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

import java.io.File

const val FRAG_DASHBOARD: Int = 0
 var IS_CHECKED_IN_TARGET_PAGE: Boolean = false
const val FRAG_CUSTOMER_LIST: Int = 1
var PAGE_INDICATOR: Int = 0
var IS_FILTER_TANSITION: Boolean = false
var is_like:Boolean= false
var value_for:String= ""
var PAGE_PREV_INDICATOR: Int = 0
val RESULT_CAR_QR_SCAN_TAB = 2222
val RESULT_LINE_QR_SCAN = 1111
val RESULT_CAR_QR_SCAN_FROM_CUSTOMER_DETAILS = 3333
val SERVER_SEND_FILE_SIZE_LIMIT = 1000000
val RESULT_CAR_QR_SCAN_FROM_CUSTOMER_EDIT = 4444
var fragmentStack: Fragment? =  null
 var mFragManager: FragmentManager? = null
 var fragTransaction: FragmentTransaction? = null
var image_update:String?=""
const val FRAG_TOP: Int = 1
const val FRAG_STORE: Int = 2
const val FRAG_CATEGORY: Int = 6
const val FRAG_ORDER: Int = 7
const val FRAG_ORDER_DETAILS: Int = 8
const val FRAG_SEARCH: Int = 10
const val FRAG_PROFILE_UPDATE: Int = 9
const val FRAG_CHANGE_PASSWORD: Int = 11
const val FRAG_NEWSFEED: Int = 12
const val FRAG_UPDATE_PURCHASE: Int = 13
const val FRAG_PRODUCT: Int = 14
const val FRAG_CREATE_PRODUCT: Int = 15
const val FRAG_UPDATE_PRODUCT: Int = 16
const val FRAG_NOTICE_DETAILS: Int = 17
const val FRAG_VIEW_DELIVERY: Int = 18
const val FRAG_DELIVERY_FOR: Int = 19
const val FRAG_MESSAGE: Int = 20
const val FRAG_WISHLIST: Int = 3
const val FRAG_SETTINGS: Int = 4
const val FRAG_NOTICE: Int = 5
//deep link paramenters
const val TAG_ACTION: String = "action"
const val ACTION_OPEN_APP: String = "open"


const val CAR_LIGHT: Int = 0
const val CAR_STANDARD: Int = 1
const val FRAG_SCAN: Int = 2
const val FRAG_ALERT: Int = 3
const val FRAG_DELIVERY: Int = 4
const val FRAG_ALERT_DETAILS: Int = 5
const val FRAG_CUSTOMER_DETAILS = 6
const val FRAG_CUSTOMER_LIST_FILTER = 18
const val FRAG_CREATE_NEW_DELIVERY = 7
const val FRAG_DELIVERY_TARGET = 8
const val FRAG_EDIT_DELIVERY = 9
const val FRAG_VEHICLE_INFORMATION = 10
const val FRAG_EDIT_CUSTOMER_INFORMATION = 11
const val FRAG_EDIT_CUSTOMER_LIST = 12
const val FRAG_COUPON_USAGE_HISTORY = 13
const val FRAG_CUSTOMER_INFORMATION = 14
const val FRAG_CUSTOMER_DETAIL_INFORMATION = 15
const val FRAG_CUSTOMER_DETAIL_EDIT_MEMO = 16
const val FRAG_CUSTOMER_LIST_AFTER_FILTER = 19
const val FRAG_CREATE_CUSTOMER_NEW = 17
var temporary_profile_picture: File? = null
var temporary_profile_picture_uri: Uri? = null
const val SERVER_SEND_FILE_SIZE_MAX_LIMIT = 7000000
const val COMPRESS_QUALITY = 60
var IS_DETAILS_PAGE = true
var value: String = ""
var data_for: String = ""
var data_for_memo: String = ""
var data_for_edit: String = ""
var data_for_vehicle_edit: String = ""
var data_delivery: String = ""

//INTENT Extra
const val EXTRAS_TAG_SEARCH_TXT = "searchText"
const val EXTRAS_TAG_CUSTOMER = "customer"


enum class State {
    DONE, LOADING, ERROR
}

// custome param
const val PARAM_MILEAGE_DATE = "mileage_date"
const val PARAM_PHONE_NUMBER = "phone_number"
const val PARAM_MOBILE_PHONE_NUMBER = "mobile_phone_number"
const val PARAM_KANA = "kana"
const val PARAM_MILEAGE = "mileage"

const val PARAM_POST_CODE = "postcode"
const val PARAM_PREFECTURE = "prefecture_id"
const val PARAM_CITY = "city"
const val PARAM_TOWN = "town"
const val PARAM_ADDRESS = "address"
const val PARAM_EMAIL = "email"
const val PARAM_CUSTOMER = "customer"
const val PARAM_PASSWORD = "password"
const val PARAM_BUILDING_ROOM_NUMBER = "building_room_number"
const val PARAM_NUMBER_PLATE_TOWN = "number_plate_town"
const val PARAM_NUMBER_PLATE_CLASS_NUMBER = "number_plate_class_number"
const val PARAM_NUMBER_PLATE_HIRAGANA = "number_plate_hiragana"
const val PARAM_NUMBER_PLATE_VEHICLE_NUMBER = "number_plate_vehicle_number"
const val PARAM_DELIVERY_FLAG = "delivery_flag"
const val PARAM_MEMO = "memo"
const val PARAM_CLASIFICATION = "classfication"
//const val value= "classfication"
// params for delivery
// params for delivery


const val PARAM_LINE_ID = "line_id"
const val PARAM_LINE_IMAGE = "line_image"
const val PARAM_CORPORATE_ID = "corporate_id"
const val PARAM_COUPON_ID = "coupon_id"
const val PARAM_TITLE = "title"
const val PARAM_DELIVERY_DATE = "delivery_date"
const val PARAM_IS_IMMEDIATE = "is_immediate"
const val PARAM_DELIVERY_TIME = "delivery_time"
const val PARAM_STATUS = "status"
const val PARAM_BODY = "body"
const val PARAM_IMAGE = "image"
const val PARAM_TYPE = "type"
const val PARAM_COLOR = "color"
const val PARAM_CAR_IMAGE_1 = "image1"
const val PARAM_CAR_IMAGE_2 = "image2"
const val PARAM_CAR_IMAGE_3 = "image3"
const val PARAM_CAR_IMAGE_4 = "image4"
const val PARAM_FRONT_AXILE_WEIGHT = "front_front_axile_weight"
const val PARAM_FRONT_REAR_AXILE_WEIGHT = "front_rear_axile_weight"
const val PARAM_REAR_FRONT_AXILE_WEIGHT = "rear_front_axile_weight"
const val PARAM_REAR_REAR_AXILE_WEIGHT = "rear_rear_axile_weight"
const val PARAM_ID = "id"
const val PARAM_LENGTH = "length"
const val PARAM_WIDTH = "width"
const val PARAM_HEIGHT = "height"
const val PARAM_WEIGHT = "weight"
const val PARAM_TOTAL_PAYLOAD = "total_payload"
const val PARAM_TOTAL_WEIGHT = "total_weight"
const val PARAM_FILTER_ID = "filter_id"
const val PARAM_DELIVERY_ID = "delivery_id"
const val PARAM_CUSTOMERY_IDS = "customer_ids"
const val PARAM_CUSTOMERY_IDS_BLACK_LIST = "customer_black_list_ids"
const val PARAM_MODEL_CATEGORY_CODE = "model_category_code"
const val PARAM_MODEL_CODE = "model_code"
const val PARAM_CHASSIS_NUMBER = "chassis_number"
const val PARAM_DELIVERY_COUNT = "delivery_count"
const val PARAM_OWNER_NAME = "owner_name"
const val PARAM_OWNER_ADDRESS = "owner_address"
const val PARAM_USER_NAME = "user_name"
const val PARAM_USER_ADDRESS = "user_address"


const val RESERVATION = "1"
const val DELIVERED = "2"
const val DELIVERY_DATA = "delivery"

const val PARAM_NAME = "name"
const val PARAM_CORPORATE_NAME = "corporate_name"
const val PARAM_STORE_NAME = "store_name"
const val PARAM_STORE_CODE = "store_code"
const val PARAM_GROUP = "group"
const val PARAM_SERVICE_ID = "service_id"
const val PARAM_BIRTHDAY = "birthday"
const val PARAM_SEX = "sex"
const val PARAM_AGE = "age"
const val TOTAL_CUSTOMER = "total_customer"
const val PARAM_FORM = "form"
const val PARAM_CREATED = "created"
const val PARAM_LAST_DELIVERED_DATE = "last_delivered_date"
const val PARAM_LAST_REFUELING_DATE = "last_refueling_date"
const val PARAM_LAST_HAND_WASH_DATE = "last_hand_wash_date"
const val PARAM_LAST_MACHINE_WASH_DATE = "last_machine_wash_date"
const val PARAM_LAST_COATING_DATE = "last_coating_date"
const val PARAM_LAST_OIL_CHANGE_DATE = "last_oil_change_date"
const val PARAM_LAST_WHEEL_CHANGE_DATE = "last_wheel_change_date"
const val PARAM_LAST_INSPECTION_DATE = "last_inspection_date"
const val PARAM_LAST_MAINTENANCE_DATE = "last_maintenance_date"
const val PARAM_CAR_MAKER = "maker"
const val PARAM_CAR_MAKER_DELIVERY = "car_maker"
const val PARAM_CAR_NAME = "car_name"
const val PARAM_CAR_MODEL = "model"
const val PARAM_ENGINE_MODEL = "engine_model"
const val PARAM_CAR_DISPLACEMENT = "displacement"
const val PARAM_CAR_DISPLACEMENT_DELIVERY = "car_displacement"
const val PARAM_CAR_TYPE = "type"
const val PARAM_CAR_TYPE_DELIVERY = "car_type"
const val PARAM_FIRST_REGISTER_DATE = "first_register_date"
const val PARAM_REGISTER_DATE = "register_date"
const val PARAM_EXPIRED_START = "expired_start"
const val PARAM_EXPIRED_END = "expired_end"
const val PARAM_EXPIRED = "expired"
const val PARAM_IS_PRIVATE = "is_private"
const val PARAM_USAGE = "usage"
const val PARAM_FUEL_TYPE = "fuel_type"
const val PARAM_CAPACITY = "capacity"
const val PARAM_AUTOMOBILE_RANK = "automobile_rank"
const val PARAM_MODEL_YEAR = "model_year"
const val PARAM_GRADE = "grade"
const val PARAM_MILEAGE_ESTIMATE = "mileage_estimate"
const val PARAM_PURCHASE_LOG_ID = "purchase_log_id"
const val PARAM_FLAG = "flag"
const val PARAM_PURCHASE_DATE = "purchase_date"


