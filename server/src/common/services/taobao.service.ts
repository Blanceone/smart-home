import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import axios from 'axios';
import * as crypto from 'crypto';

interface TaobaoItemDetail {
  item_id: string;
  title: string;
  pict_url: string;
  reserve_price: string;
  zk_final_price: string;
  coupon_amount: string;
  coupon_start_time: string;
  coupon_end_time: string;
  commission_rate: string;
}

interface PurchaseUrlResult {
  purchaseUrl: string;
  price: number;
  originalPrice: number;
  coupon: number | null;
  commissionRate: number;
  expiresAt: Date;
}

@Injectable()
export class TaobaoService {
  private readonly appKey: string;
  private readonly appSecret: string;
  private readonly adzoneId: string;

  constructor(private configService: ConfigService) {
    this.appKey = this.configService.get<string>('TAOBAO_APP_KEY', 'your_taobao_app_key');
    this.appSecret = this.configService.get<string>('TAOBAO_APP_SECRET', '');
    this.adzoneId = this.configService.get<string>('TAOBAO_ADZONE_ID', '');
  }

  private sign(params: Record<string, string>): string {
    const sortedParams = Object.keys(params)
      .sort()
      .map(key => `${key}${params[key]}`)
      .join('');
    
    return crypto
      .createHmac('md5', this.appSecret)
      .update(sortedParams)
      .digest('hex')
      .toUpperCase();
  }

  private async callTaobaoApi(method: string, params: Record<string, string>): Promise<any> {
    const now = new Date();
    const timestamp = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`;
    
    const commonParams = {
      app_key: this.appKey,
      method: method,
      format: 'json',
      v: '1.0',
      sign_method: 'md5',
      timestamp: timestamp,
    };

    const allParams: Record<string, string> = { ...commonParams, ...params };
    allParams.sign = this.sign(allParams);

    try {
      const response = await axios.post(
        'https://gw.api.taobao.com/router/rest',
        allParams,
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          timeout: 10000,
        }
      );
      return response.data;
    } catch (error) {
      console.error('Taobao API call failed:', error.message);
      return null;
    }
  }

  async getItemDetail(itemId: string): Promise<TaobaoItemDetail | null> {
    const params = {
      method: 'taobao.tbk.item.info.get',
      fields: 'item_id,title,pict_url,reserve_price,zk_final_price,coupon_amount,coupon_start_time,coupon_end_time,commission_rate',
      item_id: itemId,
    };

    const result = await this.callTaobaoApi('taobao.tbk.item.info.get', params);
    
    if (result && result.tbk_item_info_get_response) {
      return result.tbk_item_info_get_response.results;
    }
    return null;
  }

  async generatePurchaseUrl(itemId: string, deviceName?: string): Promise<PurchaseUrlResult> {
    const params = {
      method: 'taobao.tbk.item.convert',
      fields: 'num_iid,click_url',
      num_iids: itemId,
      adzone_id: this.adzoneId,
    };

    const result = await this.callTaobaoApi('taobao.tbk.item.convert', params);
    
    let clickUrl = '';
    if (result && result.tbk_item_convert_response && result.tbk_item_convert_response.results) {
      clickUrl = result.tbk_item_convert_response.results[0]?.click_url || '';
    }

    const itemDetail = await this.getItemDetail(itemId);
    
    const originalPrice = itemDetail 
      ? parseFloat(itemDetail.reserve_price) 
      : 0;
    
    const finalPrice = itemDetail 
      ? parseFloat(itemDetail.zk_final_price) 
      : originalPrice;
    
    const couponAmount = itemDetail && itemDetail.coupon_amount 
      ? parseFloat(itemDetail.coupon_amount) 
      : 0;
    
    const commissionRate = itemDetail 
      ? parseFloat(itemDetail.commission_rate) / 10000 
      : 0;

    if (!clickUrl) {
      clickUrl = `https://s.click.taobao.com/${itemId}`;
    }

    return {
      purchaseUrl: clickUrl,
      price: finalPrice - couponAmount,
      originalPrice: finalPrice,
      coupon: couponAmount > 0 ? couponAmount : null,
      commissionRate,
      expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000),
    };
  }

  async searchItems(keyword: string, page: number = 1, pageSize: number = 20): Promise<any> {
    const params = {
      method: 'taobao.tbk.item.get',
      fields: 'num_iid,title,pict_url,reserve_price,zk_final_price,commission_rate,commission_volume',
      q: keyword,
      page_no: page.toString(),
      page_size: pageSize.toString(),
      adzone_id: this.adzoneId,
    };

    const result = await this.callTaobaoApi('taobao.tbk.item.get', params);
    
    if (result && result.tbk_item_get_response) {
      return result.tbk_item_get_response.results;
    }
    return { n_tbk_item: [] };
  }

  isConfigured(): boolean {
    return !!this.appSecret && this.appSecret !== 'your_taobao_app_secret';
  }
}
