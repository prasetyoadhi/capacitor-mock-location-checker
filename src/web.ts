import { WebPlugin } from '@capacitor/core';

import type { MockLocationCheckerPlugin, CheckMockResult, DeviceInfo } from './definitions';

export class MockLocationCheckerWeb extends WebPlugin implements MockLocationCheckerPlugin {
  isRooted(): Promise<{ isRooted: boolean; }> {
    throw new Error('Method not implemented.');
  }
  isRootedWithBusyBox(): Promise<{ isRooted: boolean; }> {
    throw new Error('Method not implemented.');
  }
  isRootedWithEmulator(): Promise<{ isRooted: boolean; }> {
    throw new Error('Method not implemented.');
  }
  isRootedWithBusyBoxWithEmulator(): Promise<{ isRooted: boolean; }> {
    throw new Error('Method not implemented.');
  }
  whatIsRooted(action: string): Promise<{ isRooted: boolean; }> {
    throw new Error('Method not implemented. action: ' + action);
  }
  getDeviceInfo(): Promise<DeviceInfo> {
    throw new Error('Method not implemented.');
  }
  checkMockGeoLocation(): Promise<CheckMockResult> {
    throw new Error('Method not implemented.');
  }
  isLocationFromMockProvider(): Promise<Boolean> {
    throw new Error('Method not implemented.');
  }
  goToMockLocationAppDetail(options: { packageName: string; }): Promise<void> {
    throw new Error('Method not implemented.' + options);
  }
  async checkMock(options: { whiteList: Array<string>; }): Promise<CheckMockResult> {
    throw new Error('Method not implemented. options: ' + options);
  }
}
