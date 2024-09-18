import { WebPlugin } from '@capacitor/core';

import type { MockLocationCheckerPlugin, CheckMockResult } from './definitions';

export class MockLocationCheckerWeb extends WebPlugin implements MockLocationCheckerPlugin {
  checkMockGeoLocation(): Promise<CheckMockResult> {
    throw new Error('Method not implemented.');
  }
  isLocationFromMockProvider(): Promise<Boolean> {
    throw new Error('Method not implemented.');
  }
  goToMockLocationAppDetail(options: { packageName: string; }): Promise<void> {
    throw new Error('Method not implemented.'+options);
  }
  async checkMock(options: { whiteList: Array<string>; }): Promise<CheckMockResult> {
    throw new Error('Method not implemented.'+options);
  }
}
