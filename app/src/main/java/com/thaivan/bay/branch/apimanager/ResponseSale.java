package com.thaivan.bay.branch.apimanager;

public class ResponseSale {

    public static String getDescription(String code){

        String resDescEN = "";
        String resDescTH = "";

        switch (code){
            case "00":
                resDescEN = "Success";
                resDescTH = "ทำรายการสำเร็จ";
                break;
            case "01":
                resDescEN = "Missing required data";
                resDescTH = "เกิดข้อผิดพลาด ไม่สามารถทำรายการได้";
                break;
            case "02":
                resDescEN = "Data format error";
                resDescTH = "รูปแบบข้อมูลไม่ถูกต้อง";
                break;
            case "03":
                resDescEN = "Card not found";
                resDescTH = "ไม่พบหมายเลขบัตร";
                break;
            case "04":
                resDescEN = "Invalid card status";
                resDescTH = "ไม่สามารถใช้งานบัตรนี้ได้";
                break;
            case "05":
                resDescEN = "Card expired";
                resDescTH = "บัตรหมดอายุการใช้งาน";
                break;
            case "06":
                resDescEN = "Renew card required";
                resDescTH = "บัตรหมดอายุการใช้งาน\nกรุณาต่ออายุบัตร";
                break;
            case "07":
                resDescEN = "Topup data not found";
                resDescTH = "บัตรหมดอายุการใช้งาน\nกรุณาเติมเงิน";
                break;
            case "08":
                resDescEN = "Incorrect requested data";
                resDescTH = "เกิดข้อผิดพลาด ไม่สามารถทำรายการได้";
                break;
            case "09":
                resDescEN = "Sale transaction not found";
                resDescTH = "ไม่พบรายการขาย";
                break;
            case "10":
                resDescEN = "Sale data not match";
                resDescTH = "รายการขายไม่ถูกต้อง";
                break;
            case "11":
                resDescEN = "Authorize transaction not found";
                resDescTH = "ไม่พบรายการขาย";
                break;
            case "12":
                resDescEN = "Authorize data not match";
                resDescTH = "รายการขายไม่ถูกต้อง";
                break;
            case "13":
                resDescEN = "Card has already been used";
                resDescTH = "บัตรนี้เปิดใช้งานเรียบร้อยแล้ว";
                break;
            case "14":
                resDescEN = "Terminal not found";
                resDescTH = "ไม่พบ Terminal ID";
                break;
            case "15":
                resDescEN = "Merchant not found";
                resDescTH = "ไม่พบ Merchant ID";
                break;
            case "16":
                resDescEN = "Terminal not active";
                resDescTH = "Terminal ID ยังไม่เปิดใช้งาน";
                break;
            case "17":
                resDescEN = "Invalid cardRef";
                resDescTH = "เกิดข้อผิดพลาด ไม่สามารถทำรายการได้";
                break;
            case "18":
                resDescEN = "Invalid card type(The card type is not allow to use on this bus type)";
                resDescTH = "ไม่สามารถใช้งานบัตรนี้ได้";
                break;
            case "19":
                resDescEN = "ไม่พบข้อมูลสายรถเมล์";
                resDescTH = "ไม่พบข้อมูลสายรถเมล์";
                break;
            case "20":
                resDescEN = "Card type not support renewal";
                resDescTH = "บัตรนี้ไม่สามารถต่ออายุบัตรได้";
                break;
            case "21":
                resDescEN = "Not in student card renew period";
                resDescTH = "ระบบสามารถต่ออายุบัตรได้ล่วงหน้า 3 เดือนก่อนวันหมดอายุเท่านั้น";
                break;
            case "22":
                resDescEN = "Data not found";
                resDescTH = "ไม่พบข้อมูล";
                break;
            case "97":
                resDescEN = "Unauthorized";
                resDescTH = "เกิดข้อผิดพลาด ไม่สามารถทำรายการได้";
                break;
            case "98":
                resDescEN = "Database error";
                resDescTH = "ไม่สามารถติดต่อระบบงานได้";
                break;
            case "99":
                resDescEN = "Internal error";
                resDescTH = "ไม่สามารถติดต่อระบบงานได้";
                break;

        }


        return resDescTH;
    }
}
