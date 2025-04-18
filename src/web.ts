import { WebPlugin } from '@capacitor/core';

import type { MockLocationCheckerPlugin, CheckMockResult } from './definitions';

export class MockLocationCheckerWeb extends WebPlugin implements MockLocationCheckerPlugin {
  isLocationFromMockProvider(): Promise<Boolean> {
    throw new Error('Method not implemented.');
  }
  async checkMock(options: { whiteList: Array<string>; }): Promise<CheckMockResult> {
    throw new Error('Method not implemented. options: ' + options);
  }
}
