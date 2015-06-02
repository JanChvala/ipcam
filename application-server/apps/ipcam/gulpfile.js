'use strict';

var gulp = require('gulp');

var moduleName = 'ipcam';

gulp.paths = {
  src: 'src',
  dist: './../../server/apps/' + moduleName + '/public',
  tmp: '.tmp',
  config: 'config',
  moduleName: 'jch-app.' + moduleName,
  e2e: 'e2e'
};

require('require-dir')('./gulp');

gulp.task('default', ['clean'], function () {
    gulp.start('build');
});
